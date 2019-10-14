package com.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.entity.Permission;
import com.auth.entity.Role;
import com.auth.repository.RoleRepository;
import com.auth.service.RoleService;
import com.auth.vo.ResponseVO;
import com.auth.vo.RoleVO;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ResponseVO findAllRoleVO() {
        List<Role> rolePOList = roleRepository.findAll();
        List<RoleVO> roleVOList = new ArrayList<>();
        rolePOList.forEach(rolePO->{
            RoleVO roleVO = new RoleVO();
            BeanUtils.copyProperties(rolePO,roleVO);
            roleVOList.add(roleVO);
        });
        return ResponseVO.success(roleVOList);
    }

    @Override
    public Role findById(Integer id) {
        return roleRepository.findById(id).get();
    }
}