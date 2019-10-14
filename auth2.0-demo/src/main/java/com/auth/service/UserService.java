package com.auth.service;
import java.util.List;

import com.auth.dto.LoginUserDTO;
import com.auth.dto.UserDTO;
import com.auth.vo.ResponseVO;
import com.auth.vo.UserVO;

/**
 * @description 用户业务接口
 * @author Zhifeng.Zeng
 * @date 2019/2/21 14:06
 */
public interface UserService {

    /**
     * @description 添加用户
     */
    void addUser(UserDTO userDTO) throws Exception;

    /**
     * 删除用户
     * @param id
     */
    void deleteUser(Integer id) throws Exception;

    /**
     * @description 修改用户信息
     * @param userDTO
     */
    void updateUser(UserDTO userDTO);

    /**
     * @description 获取所有用户列表VO
     * @return
     */
    ResponseVO<List<UserVO>> findAllUserVO();

    /**
     * @description 用户登录
     * @return
     */
    ResponseVO login(LoginUserDTO loginUserDTO);

}