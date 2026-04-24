package com.campus.donation.module.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.user.dto.LoginDTO;
import com.campus.donation.module.user.dto.RegisterDTO;
import com.campus.donation.module.user.entity.SysUser;
import com.campus.donation.module.user.mapper.SysUserMapper;
import com.campus.donation.module.user.service.SysUserService;
import com.campus.donation.module.user.vo.LoginVO;
import com.campus.donation.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SysUserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        SysUser existUser = lambdaQuery()
                .eq(SysUser::getUsername, registerDTO.getUsername())
                .one();
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建用户
        SysUser user = BeanUtil.copyProperties(registerDTO, SysUser.class);
        // 密码加密
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus(1); // 默认正常状态

        // 3. 保存用户
        save(user);
        log.info("用户注册成功: username={}, userType={}", user.getUsername(), user.getUserType());
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 查询用户
        SysUser user = getUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用,请联系管理员");
        }

        // 4. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());

        // 5. 返回登录结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setUserType(user.getUserType());

        log.info("用户登录成功: username={}, userType={}", user.getUsername(), user.getUserType());
        return loginVO;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();
    }

}
