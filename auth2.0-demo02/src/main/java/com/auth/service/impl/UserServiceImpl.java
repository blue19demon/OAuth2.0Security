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

	private static final String API_PREFIX = "http://127.0.0.1:7777/";

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


	/**
	 * @description oauth2客户端刷新token
	 * @param refreshToken
	 * @date 2019/03/05 14:27:22
	 * @author Zhifeng.Zeng
	 * @return
	 */	
	@Override
	public ResponseVO<Token> refreshToken(String refreshToken) {
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		paramMap.add("client_id", OAuth2Config.CLIENT_ID);
		paramMap.add("client_secret", OAuth2Config.CLIENT_SECRET);
		paramMap.add("refresh_token", refreshToken);
		paramMap.add("grant_type", OAuth2Config.GRANT_TYPE[2]);
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
		return ResponseVO.success(token);
	}
	@Override
	public ResponseVO<Token> getToken(String code) {
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
	
	@Override
	public ResponseVO<LoginUserVO> currrentUserInfo(String token) {
		String api = API_PREFIX + "/oauth/check_token?token=%s";
		ResponseEntity<String> obj = restTemplate.exchange(String.format(api, token), HttpMethod.GET, null, String.class);
		String currentName=JSONObject.parseObject(obj.getBody()).getString("user_name");
		log.info("当前账号=="+currentName);
		LoginUserVO loginUserVO = new LoginUserVO();
		User userPO = userRepository.findUserByAccount(currentName);
		BeanUtils.copyPropertiesIgnoreNull(userPO, loginUserVO);
		return ResponseVO.success(loginUserVO);
	}

}