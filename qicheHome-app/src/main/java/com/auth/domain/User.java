package com.auth.domain;
import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = -8478114427891717226L;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户名
     */
    private String name;
    /**
     * 性别
     */
    private String gender;
    /**
     * 头像
     */
    private String headImageUrl;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getHeadImageUrl() {
		return headImageUrl;
	}
	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}
}