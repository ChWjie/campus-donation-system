package com.campus.donation.module.charity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.charity.entity.CharityQualification;
import com.campus.donation.module.charity.mapper.CharityQualificationMapper;
import com.campus.donation.module.charity.service.CharityQualificationService;
import com.campus.donation.module.user.entity.SysUser;
import com.campus.donation.module.user.service.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公益资质审核服务实现
 */
@Service
public class CharityQualificationServiceImpl extends ServiceImpl<CharityQualificationMapper, CharityQualification> implements CharityQualificationService {

    private final SysUserService userService;

    public CharityQualificationServiceImpl(SysUserService userService) {
        this.userService = userService;
    }

    @Override
    public Page<CharityQualification> getQualificationPage(Integer current, Integer size, Integer status) {
        Page<CharityQualification> page = new Page<>(current, size);
        LambdaQueryWrapper<CharityQualification> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(CharityQualification::getStatus, status);
        }
        wrapper.orderByDesc(CharityQualification::getCreateTime);
        return (Page<CharityQualification>) baseMapper.selectQualificationPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(Long id, Integer status, String reason) {
        CharityQualification qualification = getById(id);
        if (qualification == null) {
            throw new BusinessException("申请记录不存在");
        }
        
        qualification.setStatus(status);
        qualification.setRejectReason(reason);
        updateById(qualification);
        
        // 如果审核通过，自动更新用户类型为公益对接员(3)
        if (status == 1) {
            SysUser user = new SysUser();
            user.setId(qualification.getUserId());
            user.setUserType(3);
            userService.updateById(user);
        }
    }

    @Override
    public void submit(CharityQualification qualification) {
        // 检查是否已有待审核或已通过的申请
        LambdaQueryWrapper<CharityQualification> wrapper = new LambdaQueryWrapper<CharityQualification>()
                .eq(CharityQualification::getUserId, qualification.getUserId())
                .in(CharityQualification::getStatus, 0, 1);
        
        if (count(wrapper) > 0) {
            throw new BusinessException("已有待审核或已通过的申请，请勿重复提交");
        }
        
        qualification.setStatus(0);
        save(qualification);
    }

    @Override
    public java.util.List<CharityQualification> getVerifiedList() {
        return list(new LambdaQueryWrapper<CharityQualification>()
                .eq(CharityQualification::getStatus, 1)
                .orderByDesc(CharityQualification::getCreateTime));
    }
}
