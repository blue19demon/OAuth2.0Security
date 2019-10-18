package com.auth.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.auth.domain.LoginUserVO;
import com.auth.domain.User;
import com.auth.service.ApiServiceImpl;
import com.auth.utils.AssertUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 用户权限管理
 * @author Zhifeng.Zeng
 * @date 2019/4/19 13:58
 */
@Controller
@Slf4j
public class LoginController {

	@Value("${auth.url}")
	private String authUrl;
	@Value("${auth.appId}")
	private String appId;
	@Value("${auth.appSecret}")
	private String appSecret;
	@Value("${auth.notifyUrl}")
	private String notifyUrl;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private ApiServiceImpl apiService;
	
	private static final String CURRENT_USER="curentUserInfo";
	
	@RequestMapping("/logout")
	public String logout(Model model) {
		httpSession.invalidate();
		return "redirect:/login";
	}
	@RequestMapping("/login")
	public String login(Model model) {
		return "login";
	}
	@RequestMapping("/home")
	public String home(Model model) {
		LoginUserVO user = (LoginUserVO) httpSession.getAttribute(CURRENT_USER);
		if(user==null) {
			 return "redirect:/login?error=请先登录";
		}
		return "home";
	}

	@RequestMapping("/toAuthLogin")
	public String toAuthLogin(Model model) {
		/**
		 * additionalParam
		 * 标识额外原样返回的参数
		 */
		//简单参数
		//String additionalParam="xkkkkkkkkkkkkkkkklll";
		//复杂参数
		Param param=new Param();
		param.setId(2);
		param.setName("张三");
		param.setItems(Arrays.asList(new Item("好好"),new Item("1AJDJ2")));
		//String additionalParam=Base64.encodeBase64String(JSONObject.toJSONString(param).getBytes());
		//没有参数
		String additionalParam=null;
		String authLoginUrl = String.format(authUrl, notifyUrl, appId, appSecret,additionalParam);
		return "redirect:" + authLoginUrl;
	}

	@Data
	@ToString
	public static class Param{
		private String name;
		private Integer id;
		private List<Item> items;
		
	}
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class Item{
		private String name;
		
	}
	@RequestMapping("/success/callback")
	public String callback(String code,String additionalParam, Model model) {
		log.info("i received code=" + code);
		log.info("additionalParam=" + additionalParam);
		/**
		 * String additionalParamReal = new String(org.apache.commons.codec.binary.Base64.decodeBase64(additionalParam));
		System.out.println(additionalParamReal);
		System.out.println(JSONObject.parseObject(additionalParamReal, Param.class));
		 */
		if(code==null||"".equals(code)||"null".equals(code)) {
			throw new RuntimeException("用户取消授权");
		}
		String accessToken = apiService.getAccessToken(code);
		//保存用户信息
		//apiService.saveUser(accessToken);
		//获取用户列表信息
		List<User> userLists =apiService.getUserLists(accessToken);
		log.info("userList="+JSONObject.toJSONString(userLists,true));
		//当前用户信息
		//String token =AssertUtils.extracteToken(accessToken);
		//System.out.println(token);
		LoginUserVO user = apiService.currrentUserInfo(accessToken);
		log.info("user=" + JSONObject.toJSONString(user, true));
		model.addAttribute("userInfo", user);
		synchronizeToSession(user);
		return "home";
	}
	private void synchronizeToSession(LoginUserVO user) {
		httpSession.setAttribute(CURRENT_USER, user);
	}

}