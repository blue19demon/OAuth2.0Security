package com.auth.service;

import java.util.List;

import com.auth.domain.LoginUserVO;
import com.auth.domain.User;

public interface ApiService {

	public String getAccessToken(String code);
	
	public void saveUser(String token);
	
	public LoginUserVO currrentUserInfo(String token);
	
	public List<User> getUserLists(String token);
}
