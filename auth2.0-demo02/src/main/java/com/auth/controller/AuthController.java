package com.auth.controller;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.auth.service.UserService;
import com.auth.vo.ResponseVO;
import com.auth.vo.UserVO;

import io.netty.handler.codec.base64.Base64Encoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 用户权限管理
 * @author Zhifeng.Zeng
 * @date 2019/4/19 13:58
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/auth/")
public class AuthController {


    @Autowired
    private UserService userService;
    
    /**
     * @param code
     * @return
     */
    @RequestMapping("callBack")
    public String callBack(String code,HttpServletResponse response) throws Exception {
    	//通过code换token
    	log.info("code="+code);
    	ResponseVO responseVO =userService.getUserInfos(code);
    	final Base64 base64 = new Base64();
        return "redirect:/auth/home?data="+new String(base64.encodeToString(JSONObject.toJSONString(responseVO.getData()).getBytes("UTF-8")));
    }
    /**
     * @param userDTO
     * @return
     */
    @RequestMapping("home")
    public String home(String data,Model model) throws Exception {
    	//通过code换token
    	log.info(" home data="+data);
    	final Base64 base64 = new Base64();
    	String afterDecode = new String(base64.decode(data),"UTF-8");
    	log.info(" home afterDecode="+afterDecode);
    	model.addAttribute("data", JSONObject.parseObject(afterDecode, ResponseVO.class));
    	return "home";
    }

}