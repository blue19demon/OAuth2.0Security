package com.auth.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.auth.repository.AppInfoRepository;
import com.auth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
 
@Controller
@SessionAttributes("authorizationRequest")
@Slf4j
public class BootGrantController {
	@Autowired
    private AppInfoRepository appInfoRepository;
	@Autowired
    private UserRepository userRepository;
    
	@RequestMapping("/custom/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request,HttpSession session) throws Exception {
 
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
 
 
        ModelAndView view = new ModelAndView();
        view.setViewName("base-grant");
 
        view.addObject("clientId", authorizationRequest.getClientId());
 
        view.addObject("scopes",authorizationRequest.getScope());
        log.info(JSONObject.toJSONString(authorizationRequest, true));
        Map<String, String> reqMap=authorizationRequest.getRequestParameters();
        if(reqMap==null) {
        	throw new IllegalArgumentException("参数错误");
        }
        if(!reqMap.containsKey("appId")) {
        	throw new IllegalArgumentException("appId参数不能为空");
        }
        if(!reqMap.containsKey("appSecret")) {
        	throw new IllegalArgumentException("appSecret参数不能为空");
        }
        view.addObject("appInfo", appInfoRepository.findAppInfoByAppId(reqMap.get("appId")));
        /**
         * 获取当前登录的用户
         */
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        System.out.println(JSONObject.toJSONString(authentication,true));
        view.addObject("userInfo", userRepository.findUserByAccount(authentication.getName()));
        return view;
    }
}