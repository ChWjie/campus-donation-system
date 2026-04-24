package com.campus.donation.module.charity.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.donation.module.charity.entity.CharityQualification;

/**
 * 公益资质审核服务接口
 */
public interface CharityQualificationService extends IService<CharityQualification> {

    /**
     * 分页查询资质申请
     */
    Page<CharityQualification> getQualificationPage(Integer current, Integer size, Integer status);

    /**
     * 审核资质
     */
    void audit(Long id, Integer status, String reason);

    /**
     * 提交资质申请
     */
    void submit(CharityQualification qualification);

    /**
     * 获取所有已审核通过的公益组织
     */
    java.util.List<CharityQualification> getVerifiedList();
}
