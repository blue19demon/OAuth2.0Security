package com.funtl.oauth2.server.service;
import java.util.List;

import com.funtl.oauth2.server.domain.TbPermission;

public interface TbPermissionService {
    default List<TbPermission> selectByUserId(Long userId) {
        return null;
    }
}