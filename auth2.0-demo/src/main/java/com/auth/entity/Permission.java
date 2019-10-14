package com.auth.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.auth.domain.Base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "um_t_permission")
public class Permission extends Base implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//权限名称
    private String name;

    //权限描述
    private String descritpion;

    //授权链接
    private String url;

    //父节点id
    private Integer pid;
}