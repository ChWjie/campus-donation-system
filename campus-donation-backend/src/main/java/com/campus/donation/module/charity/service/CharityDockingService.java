package com.campus.donation.module.charity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.donation.module.charity.dto.CharityDockingDTO;
import com.campus.donation.module.charity.entity.CharityDocking;

import java.util.List;
import java.util.Map;

/**
 * 公益对接服务接口
 */
public interface CharityDockingService extends IService<CharityDocking> {

    /**
     * 创建对接申请
     */
    CharityDocking createDocking(CharityDockingDTO dto, Long operatorId);

    /**
     * 查询对接列表
     */
    List<CharityDocking> getDockingList(Integer status, Long operatorId, boolean isAdmin);

    /**
     * 查询对接详情
     */
    CharityDocking getDockingDetail(Long id);

    /**
     * 上传反馈
     */
    void uploadFeedback(Long id, String feedbackText, String feedbackImages);

    /**
     * 完成对接
     */
    void completeDocking(Long id);

    /**
     * 获取月度对接统计
     */
    List<Map<String, Object>> getMonthlyStatistics();

    /**
     * 获取对接总数
     */
    Integer getTotalCount();
}
