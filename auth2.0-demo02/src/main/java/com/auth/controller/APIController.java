package com.auth.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth.dto.LoginUserDTO;
import com.auth.dto.UserDTO;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import com.auth.utils.AssertUtils;
import com.auth.vo.ResponseVO;

/**
 * 
 * 授权后要访问的资源
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/api")
public class APIController {
	@Autowired
	private UserService userService;

	@Autowired
	private RedisTokenStore redisTokenStore;

	 /**
   	 * @description 添加用户
   	 * @param userDTO
   	 * @return
   	 */
   	@GetMapping("/refreshToken")
   	@ResponseBody
   	public ResponseVO refreshToken(String refreshToken) throws Exception {
   		return userService.refreshToken(refreshToken);
   	}
   	/**
   	 * @description 添加用户
   	 * @param userDTO
   	 * @return
   	 */
   	@GetMapping("/currrentUserInfo")
	@ResponseBody
   	public ResponseVO currrentUserInfo(String token) throws Exception {
   		return userService.currrentUserInfo(token);
   	}
   	
    /**
	 * @description 添加用户
	 * @param userDTO
	 * @return
	 */
	@PostMapping("/user")
	@ResponseBody
	public ResponseVO add(@Valid @RequestBody UserDTO userDTO) throws Exception {
		userService.addUser(userDTO);
		return ResponseVO.success();
	}
	/**
	 * @description 获取用户列表
	 * @return
	 */
	@GetMapping("/userList")
	@ResponseBody
	public ResponseVO findAllUser() {
		return userService.findAllUserVO();
	}

	/**
	 * @description 删除用户
	 * @param id
	 * @return
	 */
	@DeleteMapping("/user/{id}")
	public ResponseVO deleteUser(@PathVariable("id") Integer id) throws Exception {
		userService.deleteUser(id);
		return ResponseVO.success();
	}

	/**
	 * @descripiton 修改用户
	 * @param userDTO
	 * @return
	 */
	@PutMapping("/user")
	public ResponseVO updateUser(@Valid @RequestBody UserDTO userDTO) {
		userService.updateUser(userDTO);
		return ResponseVO.success();
	}
	/**
	 * @description 用户注销
	 * @param authorization
	 * @return
	 */
	@GetMapping("/user/logout")
	public ResponseVO logout(@RequestHeader("Authorization") String authorization) {
		redisTokenStore.removeAccessToken(AssertUtils.extracteToken(authorization));
		return ResponseVO.success();
	}

}
