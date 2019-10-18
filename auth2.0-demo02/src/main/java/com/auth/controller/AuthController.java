package com.auth.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.auth.config.ServerConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * @description 用户权限管理
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private ServerConfig serverConfig;

	/**
	 * 这里最好的办法是使用Base64编码
	 */
	private static final String SPLIT="KKFFCC";
	private static final String SPLIT_EQ="KFC";
	/**
	 * 里面有个state字段可以原样返回
	 * 
	 * @param callback_uri
	 * @param appId
	 * @param appSecret
	 * @return
	 * @throws Exception
	 */
	//@RequestMapping("/oauthLogin")
	public String oauthLogin_old(String additionalParam, String notifyUrl, String appId, String appSecret)
			throws Exception {
		System.out.println("====additionalParam====" + additionalParam);
		if (null == additionalParam || "null".equals(additionalParam) || "".equals(additionalParam)) {
			return "redirect:" + String.format(serverConfig.getUrl()
					+ "/oauth/authorize?response_type=code&client_id=client_1&redirect_uri=%s&state=%s&appId=%s&appSecret=%s",
					serverConfig.getUrl() + "/auth/callBack", notifyUrl, appId, appSecret);
		}
		return "redirect:" + String.format(serverConfig.getUrl()
				+ "/oauth/authorize?response_type=code&client_id=client_1&redirect_uri=%s&state=%s&appId=%s&appSecret=%s",
				serverConfig.getUrl() + "/auth/callBack", notifyUrl +SPLIT+ "additionalParam"+SPLIT_EQ + additionalParam, appId,
				appSecret);
	}
	
	/**
	 * @param code
	 * @return
	 */
	//@RequestMapping("/callBack")
	public String callBack_old(String code, String state) throws Exception {
		log.info("code=" + code);
		log.info("state=" + state);
		// 说明有additionalParam参数，原样返回
		if (state.indexOf("additionalParam") != -1) {
			return "redirect:" + String.format(state.replaceAll(SPLIT, "?").replaceAll(SPLIT_EQ, "=") + "&code=%s", code);
		}
		return "redirect:" + String.format(state + "?code=%s", code);
	}
	/**
	 * 里面有个state字段可以原样返回
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/oauthLogin")
	public String oauthLogin(String additionalParam, String notifyUrl, String appId, String appSecret)
			throws Exception {
		System.out.println("====additionalParam====" + additionalParam);
		if (null == additionalParam || "null".equals(additionalParam) || "".equals(additionalParam)) {
			return "redirect:" + String.format(serverConfig.getUrl()
					+ "/oauth/authorize?response_type=code&client_id=client_1&redirect_uri=%s&state=%s&appId=%s&appSecret=%s",
					serverConfig.getUrl() + "/auth/callBack", notifyUrl, appId, appSecret);
		}
		String starteReal=(notifyUrl +"?additionalParam="+ additionalParam);
		String state= org.apache.commons.codec.binary.Base64.encodeBase64String(starteReal.getBytes());
		return "redirect:" + String.format(serverConfig.getUrl()
				+ "/oauth/authorize?response_type=code&client_id=client_1&redirect_uri=%s&state=%s&appId=%s&appSecret=%s",
				serverConfig.getUrl() + "/auth/callBack",state, appId,
				appSecret);
	}
	/**
	 * @param code
	 * @return
	 */
	@RequestMapping("/callBack")
	public String callBack(String code, String state) throws Exception {
		log.info("code=" + code);
		log.info("state 密文=" + state);
		String starteReal = new String(org.apache.commons.codec.binary.Base64.decodeBase64(state));
		log.info("state 明文=" + starteReal);
		// 说明有additionalParam参数，原样返回
		if (starteReal.indexOf("?") != -1) {
			return "redirect:" + String.format(starteReal + "&code=%s", code);
		}
		return "redirect:" + String.format(state + "?code=%s", code);
	}
	
}