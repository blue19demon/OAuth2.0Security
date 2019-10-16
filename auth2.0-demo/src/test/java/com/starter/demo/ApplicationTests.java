package com.starter.demo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.auth.Auth2BootDemoApplication;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.RoleService;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)   
@SpringBootTest(classes={Auth2BootDemoApplication.class})// 指定启动类
@Slf4j
public class ApplicationTests {
	@Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository userRepository;
    @Test
    public void testOne(){
    	String username="admin";
    	log.info("username:{}",username);
        User user = userRepository.findUserByAccount(username);
  	    log.info(JSONObject.toJSONString(user));
    }

    @Test
    public void testTwo(){
        System.out.println("test hello 2");
        TestCase.assertEquals(1, 1);
    }

    @Before
    public void testBefore(){
        System.out.println("before");
    }

    @After
    public void testAfter(){
        System.out.println("after");
    }
}