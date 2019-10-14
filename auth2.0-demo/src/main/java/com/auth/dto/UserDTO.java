package com.auth.dto;
import com.alibaba.fastjson.JSONObject;
import com.auth.domain.Base;

import lombok.Data;

/**
 * @description 添加、修改用户传输参数
 * @author Zhifeng.Zeng
 * @date 2019/4/19
 */
@Data
public class UserDTO extends Base {

    /**
     * 用户名
     */
    private String account;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 用户角色id
     */
    private Integer roleId;

    public static void main(String[] args) {
    	UserDTO userDTO=new UserDTO();
    	userDTO.setAccount("xiaohong");
    	userDTO.setName("小红");
    	userDTO.setPassword("123456");
    	userDTO.setRoleId(1);
    	userDTO.setDescription("再添加一个新用户");
    	
    	System.out.println(JSONObject.toJSONString(userDTO, true));
	}
}