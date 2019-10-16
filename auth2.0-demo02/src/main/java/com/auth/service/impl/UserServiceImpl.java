package com.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.auth.config.OAuth2Config;
import com.auth.config.ServerConfig;
import com.auth.domain.Token;
import com.auth.dto.LoginUserDTO;
import com.auth.dto.UserDTO;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.enums.ResponseEnum;
import com.auth.enums.UrlEnum;
import com.auth.repository.UserRepository;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import com.auth.utils.AssertUtils;
import com.auth.utils.BeanUtils;
import com.auth.utils.RedisUtil;
import com.auth.vo.LoginUserVO;
import com.auth.vo.ResponseVO;
import com.auth.vo.RoleVO;
import com.auth.vo.UserVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleService roleService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServerConfig serverConfig;

	@Autowired
	private RedisUtil redisUtil;

	private static final String API_PREFIX = "http://127.0.0.1:7777/api";
	@Autowired
	private RedisTokenStore redisTokenStore;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addUser(UserDTO userDTO) {
		User userPO = new User();
		User userByAccount = userRepository.findUserByAccount(userDTO.getAccount());
		if (userByAccount != null) {
			// 此处应该用自定义异常去返回，在这里我就不去具体实现了
			try {
				throw new Exception("This user already exists!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		userPO.setCreatedTime(System.currentTimeMillis());
		// 添加用户角色信息
		Role rolePO = roleService.findById(userDTO.getRoleId());
		userPO.setRole(rolePO);
		BeanUtils.copyPropertiesIgnoreNull(userDTO, userPO);
		userRepository.save(userPO);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(Integer id) {
		User userPO = userRepository.findById(id).get();
		if (userPO == null) {
			// 此处应该用自定义异常去返回，在这里我就不去具体实现了
			try {
				throw new Exception("This user not exists!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		userRepository.delete(userPO);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUser(UserDTO userDTO) {
		User userPO = userRepository.findById(userDTO.getId()).get();
		if (userPO == null) {
			// 此处应该用自定义异常去返回，在这里我就不去具体实现了
			try {
				throw new Exception("This user not exists!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		BeanUtils.copyPropertiesIgnoreNull(userDTO, userPO);
		// 修改用户角色信息
		Role rolePO = roleService.findById(userDTO.getRoleId());
		userPO.setRole(rolePO);
		userRepository.saveAndFlush(userPO);
	}

	@Override
	public ResponseVO<List<UserVO>> findAllUserVO() {
		List<User> userPOList = userRepository.findAll();
		List<UserVO> userVOList = new ArrayList<>();
		userPOList.forEach(userPO -> {
			UserVO userVO = new UserVO();
			BeanUtils.copyPropertiesIgnoreNull(userPO, userVO);
			RoleVO roleVO = new RoleVO();
			BeanUtils.copyPropertiesIgnoreNull(userPO.getRole(), roleVO);
			userVO.setRole(roleVO);
			userVOList.add(userVO);
		});
		return ResponseVO.success(userVOList);
	}

	@Override
	public ResponseVO login(LoginUserDTO loginUserDTO) {
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("client_id", OAuth2Config.CLIENT_ID);
		paramMap.add("client_secret", OAuth2Config.CLIENT_SECRET);
		paramMap.add("username", loginUserDTO.getAccount());
		paramMap.add("password", loginUserDTO.getPassword());
		paramMap.add("grant_type", OAuth2Config.GRANT_TYPE[0]);
		Token token = null;
		try {
			// 因为oauth2本身自带的登录接口是"/oauth/token"，并且返回的数据类型不能按我们想要的去返回
			// 但是我的业务需求是，登录接口是"user/login"，由于我没研究过要怎么去修改oauth2内部的endpoint配置
			// 所以这里我用restTemplate(HTTP客户端)进行一次转发到oauth2内部的登录接口，比较简单粗暴
			token = restTemplate.postForObject(serverConfig.getUrl() + UrlEnum.LOGIN_URL.getUrl(), paramMap,
					Token.class);
			LoginUserVO loginUserVO = redisUtil.get(token.getValue(), LoginUserVO.class);
			if (loginUserVO != null) {
				// 登录的时候，判断该用户是否已经登录过了
				// 如果redis里面已经存在该用户已经登录过了的信息
				// 我这边要刷新一遍token信息，不然，它会返回上一次还未过时的token信息给你
				// 不便于做单点维护
				token = oauthRefreshToken(loginUserVO.getRefreshToken());
				redisUtil.deleteCache(loginUserVO.getAccessToken());
			}
		} catch (RestClientException e) {
			try {
				e.printStackTrace();
				// 此处应该用自定义异常去返回，在这里我就不去具体实现了
				// throw new Exception("username or password error");
				return ResponseVO.error(ResponseEnum.INCORRECT_ACCONT_INFO);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		// 这里我拿到了登录成功后返回的token信息之后，我再进行一层封装，最后返回给前端的其实是LoginUserVO
		LoginUserVO loginUserVO = new LoginUserVO();
		User userPO = userRepository.findUserByAccount(loginUserDTO.getAccount());
		BeanUtils.copyPropertiesIgnoreNull(userPO, loginUserVO);
		loginUserVO.setPassword(userPO.getPassword());
		loginUserVO.setAccessToken(token.getValue());
		loginUserVO.setAccessTokenExpiresIn(token.getExpiresIn());
		loginUserVO.setAccessTokenExpiration(token.getExpiration());
		loginUserVO.setExpired(token.isExpired());
		loginUserVO.setScope(token.getScope());
		loginUserVO.setTokenType(token.getTokenType());
		loginUserVO.setRefreshToken(token.getRefreshToken().getValue());
		loginUserVO.setRefreshTokenExpiration(token.getRefreshToken().getExpiration());
		// 存储登录的用户
		redisUtil.set(loginUserVO.getAccessToken(), loginUserVO, TimeUnit.HOURS.toSeconds(1));
		return ResponseVO.success(loginUserVO);
	}

	/**
	 * @description oauth2客户端刷新token
	 * @param refreshToken
	 * @date 2019/03/05 14:27:22
	 * @author Zhifeng.Zeng
	 * @return
	 */
	private Token oauthRefreshToken(String refreshToken) {
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("client_id", OAuth2Config.CLIENT_ID);
		paramMap.add("client_secret", OAuth2Config.CLIENT_SECRET);
		paramMap.add("refresh_token", refreshToken);
		paramMap.add("grant_type", OAuth2Config.GRANT_TYPE[1]);
		Token token = null;
		try {
			token = restTemplate.postForObject(serverConfig.getUrl() + UrlEnum.LOGIN_URL.getUrl(), paramMap,
					Token.class);
		} catch (RestClientException e) {
			try {
				// 此处应该用自定义异常去返回，在这里我就不去具体实现了
				throw new Exception(ResponseEnum.REFRESH_TOKEN_INVALID.getMessage());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return token;
	}
	@Override
	public ResponseVO getToken(String code) {
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("grant_type", OAuth2Config.GRANT_TYPE[2]);
		paramMap.add("code", code);
		paramMap.add("client_id", OAuth2Config.CLIENT_ID);
		paramMap.add("client_secret", OAuth2Config.CLIENT_SECRET);
		paramMap.add("redirect_uri", serverConfig.getUrl()+"/auth/callBack");
		Token token = null;
		try {
			token = restTemplate.postForObject(serverConfig.getUrl() + UrlEnum.LOGIN_URL.getUrl(), paramMap,
					Token.class);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		return ResponseVO.success(token);
	}
	
	public String getAccessToken(String code) {
		Token token = redisUtil.get(code, Token.class);
		if(token==null) {
			ResponseVO responseVO = getToken(code);
			token = (Token) responseVO.getData();
			redisUtil.set(code, token, TimeUnit.SECONDS.toSeconds(token.getExpiresIn()));
		}
		log.info("token="+JSONObject.toJSONString(token, true));
		String authorization = AssertUtils.buildBearerToken(token.getValue());
		return authorization;
	}

	@Override
	public ResponseVO getUserInfos(String code) {
		String api = API_PREFIX + "/user";
		HttpHeaders headers = new HttpHeaders();
		String Authorization = getAccessToken(code);
		log.info("api="+api);
		log.info("Authorization="+Authorization);
		headers.add("Authorization", Authorization);
		HttpEntity<String> httpEntity = new HttpEntity<String>(null, headers);
		//{"code":1004,"message":"访问此资源需要完全的身份验证"}   token不传
		/**
		 * {
			    "code": 1001,
			    "message": "access_token无效"
			}
			token 已失效
		 */
		ResponseEntity<ResponseVO> obj = restTemplate.exchange(api.toString(), HttpMethod.GET, httpEntity, ResponseVO.class);
		return ResponseVO.success(obj.getBody());
	}

}