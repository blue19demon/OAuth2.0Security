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
@Table(name = "um_t_app_info")
public class AppInfo extends Base implements Serializable {
    private static final long serialVersionUID = -8478114427891717226L;
    private String appId;
    private String appName;
    private String appIcon;
    private String appSecret;
}