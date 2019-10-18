package com.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description 用户权限管理
 * @author Zhifeng.Zeng
 * @date 2019/4/19 13:58
 */
@Controller
public class LoginController {

	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("loginProcessUrl", "/auth/authorize");
		return "login";
	}
}