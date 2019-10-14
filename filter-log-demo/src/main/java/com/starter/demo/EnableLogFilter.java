package com.starter.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//方式一,在使用的时候，组启动类上加上这个注解;
//方式二使用配置文件src/main/resources/META-INF/spring.factories
//此时，依赖jar就可以了
//@Import(LogFilterRegistrationBean.class)
public @interface EnableLogFilter {

}
