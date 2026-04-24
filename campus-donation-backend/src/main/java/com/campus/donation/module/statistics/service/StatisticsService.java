package com.campus.donation.module.statistics.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取总体概览
     */
    Map<String, Object> getOverview();

    /**
     * 获取月度趋势
     */
    List<Map<String, Object>> getMonthlyTrend();

    /**
     * 获取旧物类型分布
     */
    List<Map<String, Object>> getItemTypeDistribution();

    /**
     * 获取公益对接统计
     */
    Map<String, Object> getDockingStatistics();

    /**
     * 获取用户个人统计
     */
    Map<String, Object> getUserStatistics(Long userId);
}
