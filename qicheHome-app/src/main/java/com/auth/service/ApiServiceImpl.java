package com.auth.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth.domain.LoginUserVO;
import com.auth.domain.RefreshTokenBean;
import com.auth.domain.ResponseVO;
import com.auth.domain.Token;
import com.auth.domain.User;
import com.auth.domain.UserDTO;
import com.auth.utils.AssertUtils;
import com.auth.utils.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ApiServiceImpl implements ApiService {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private RedisUtil redisUtil;
	/**
	 * 无需授权
	 */
	private static final String GET_TOKEN = "http://127.0.0.1:7777/thrid/getToken?code=%s";
	/**
	 * 需要授权才能访问的资源
	 */
	private static final String GET_REFRESH_TOKEN = "http://127.0.0.1:7777/api/refreshToken?refreshToken=%s";

	/**
	 * 需要授权才能访问的资源
	 */
	private static final String GET_USER_INFO = "http://127.0.0.1:7777/api/currrentUserInfo?token=%s";

	/**
	 * 需要授权才能访问的资源
	 */
	private static final String POST_SAVE_USER = "http://127.0.0.1:7777/api/user";

	/**
	 * 需要授权才能访问的资源
	 */
	private static final String GET_USER_LIST = "http://127.0.0.1:7777/api/userList";

	@Override
	public String getAccessToken(String code) {
		Token tokenFromRedis = redisUtil.get(code, Token.class);
		if (tokenFromRedis != null) {
			String tokenString = AssertUtils.buildBearerToken(tokenFromRedis.getValue());
			return tokenString;
		} else {
			String refreshTokenFromRedis = redisUtil.get("refreshToken_" + code, String.class);
			if (refreshTokenFromRedis != null) {
				// 刷新token
				return getRefreshToken(code, refreshTokenFromRedis);
			} else {
				return getToken(code);
			}
		}
	}

	/**
	 * 从网络刷新token
	 * 
	 * @param code
	 * @return
	 */
	private String getRefreshToken(String code, String refreshToken) {
		ResponseVO<Token> obj = null;
		try {
			String api = String.format(GET_REFRESH_TOKEN, refreshToken);
			log.info("getRefreshToken api=" + api);
			ResponseEntity res = restTemplate.exchange(api.toString(), HttpMethod.GET, null, ResponseVO.class);
			obj = (ResponseVO<Token>) res.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj.getData())); // 将数据转成json字符串
		Token token = JSONObject.toJavaObject(jsonObject, Token.class); // 将json转成需要的对象
		putInRedis(code, token);
		log.info("token=" + JSONObject.toJSONString(token, true));
		String tokenString = AssertUtils.buildBearerToken(token.getValue());
		return tokenString;
	}

	/**
	 * 从网络获取token
	 * 
	 * @param code
	 * @return
	 */
	private String getToken(String code) {
		ResponseVO<Token> obj = null;
		try {
			String api = String.format(GET_TOKEN, code);
			log.info("getToken api=" + api);
			ResponseEntity res = restTemplate.exchange(api.toString(), HttpMethod.GET, null, ResponseVO.class);
			obj = (ResponseVO<Token>) res.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj.getData())); // 将数据转成json字符串
		Token token = JSONObject.toJavaObject(jsonObject, Token.class); // 将json转成需要的对象
		putInRedis(code, token);
		log.info("token=" + JSONObject.toJSONString(token, true));
		String tokenString = AssertUtils.buildBearerToken(token.getValue());
		return tokenString;
	}

	private void putInRedis(String code, Token token) {
		if (token != null) {
			/**
			 * 提前五秒过期
			 */
			redisUtil.set(code, token, TimeUnit.SECONDS.toSeconds(token.getExpiresIn() - 5));
			RefreshTokenBean refreshToken = token.getRefreshToken();
			String expiration = refreshToken.getExpiration();
			Long expirationTimeLong = 0L;
			redisUtil.set("refreshToken_" + code, refreshToken.getValue(),
					TimeUnit.SECONDS.toSeconds(expirationTimeLong - 5));
		}
	}

	@Override
	public void saveUser(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		UserDTO userDTO = new UserDTO();
		userDTO.setAccount("xiaozhang");
		userDTO.setName("小张");
		userDTO.setPassword("123456");
		userDTO.setRoleId(2);
		// @RequestBody 使用JSON
		HttpEntity<String> httpEntity = new HttpEntity<String>(JSONObject.toJSONString(userDTO), headers);
		/**
		 * { "code":0, "message":"成功" }
		 */

		/**
		 * { "code":1003, "message":"该用户权限不足以访问该资源接口" }
		 */
		ResponseEntity<ResponseVO> obj = restTemplate.exchange(POST_SAVE_USER, HttpMethod.POST, httpEntity,
				ResponseVO.class);
		log.info(JSONObject.toJSONString(obj.getBody(), true));
	}

	@Override
	public LoginUserVO currrentUserInfo(String token) {
		ResponseVO<LoginUserVO> obj = null;
		try {
			String api = String.format(GET_USER_INFO, AssertUtils.extracteToken(token));
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", token);
			MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
			HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(
					paramMap, headers);
			ResponseEntity res = restTemplate.exchange(api, HttpMethod.GET, httpEntity, ResponseVO.class);
			obj = (ResponseVO<LoginUserVO>) res.getBody();
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		if (obj != null && obj.getCode() == 0) {
			JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj.getData())); // 将数据转成json字符串
			return JSONObject.toJavaObject(jsonObject, LoginUserVO.class); // 将json转成需要的对象
		}
		return null;
	}

	@Override
	public List<User> getUserLists(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);
		MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
		ResponseEntity<ResponseVO> obj = null;
		try {
			HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(
					paramMap, headers);
			obj = restTemplate.exchange(GET_USER_LIST, HttpMethod.GET, httpEntity, ResponseVO.class);
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		ResponseVO<List<User>> responseVO = obj.getBody();
		if (responseVO != null && responseVO.getCode() == 0) {
			JSONArray jsonObject = JSONArray.parseArray(JSONObject.toJSONString(responseVO.getData()));
			return JSONArray.toJavaObject(jsonObject, List.class);
		}
		return null;
	}
}
