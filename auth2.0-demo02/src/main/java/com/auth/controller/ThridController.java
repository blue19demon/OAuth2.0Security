package com.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.domain.Token;
import com.auth.service.UserService;
import com.auth.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 授权后要访问的资源
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/thrid")
@Slf4j
public class ThridController {

	@Autowired
	private UserService userService;

	

	/**
	 * @description 删除用户
	 * @param id
	 * @return
	 */
	@GetMapping("/getToken")
	public ResponseVO<Token> getToken(String code) throws Exception {
		log.info("getToken code="+code);
		return userService.getToken(code);
	}

}
