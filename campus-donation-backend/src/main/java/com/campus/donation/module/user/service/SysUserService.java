package com.campus.donation.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.donation.module.user.dto.LoginDTO;
import com.campus.donation.module.user.dto.RegisterDTO;
import com.campus.donation.module.user.entity.SysUser;
import com.campus.donation.module.user.vo.LoginVO;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户注册
     *
     * @param registerDTO 注册信息
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录结果(包含Token)
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

}
