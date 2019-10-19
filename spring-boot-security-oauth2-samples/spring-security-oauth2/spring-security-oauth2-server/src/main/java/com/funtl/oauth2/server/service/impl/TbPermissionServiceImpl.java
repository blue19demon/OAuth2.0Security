package com.funtl.oauth2.server.service.impl;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.funtl.oauth2.server.domain.TbPermission;
import com.funtl.oauth2.server.mapper.TbPermissionMapper;
import com.funtl.oauth2.server.service.TbPermissionService;

@Service
public class TbPermissionServiceImpl implements TbPermissionService {

    @Resource
    private TbPermissionMapper tbPermissionMapper;

    @Override
    public List<TbPermission> selectByUserId(Long userId) {
        return tbPermissionMapper.selectByUserId(userId);
    }
}