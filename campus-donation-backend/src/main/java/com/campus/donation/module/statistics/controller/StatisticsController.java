package com.campus.donation.module.statistics.controller;

import com.campus.donation.common.result.R;
import com.campus.donation.module.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@Tag(name = "数据统计")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Operation(summary = "总体概览")
    @GetMapping("/overview")
    public R<Map<String, Object>> getOverview() {
        Map<String, Object> overview = statisticsService.getOverview();
        return R.ok(overview);
    }

    @Operation(summary = "月度趋势")
    @GetMapping("/monthly")
    public R<List<Map<String, Object>>> getMonthlyTrend() {
        List<Map<String, Object>> trend = statisticsService.getMonthlyTrend();
        return R.ok(trend);
    }

    @Operation(summary = "旧物类型分布")
    @GetMapping("/item-type")
    public R<List<Map<String, Object>>> getItemTypeDistribution() {
        List<Map<String, Object>> distribution = statisticsService.getItemTypeDistribution();
        return R.ok(distribution);
    }

    @Operation(summary = "公益对接统计")
    @GetMapping("/docking")
    public R<Map<String, Object>> getDockingStatistics() {
        Map<String, Object> stats = statisticsService.getDockingStatistics();
        return R.ok(stats);
    }

    @Operation(summary = "个人统计")
    @GetMapping("/user")
    public R<Map<String, Object>> getUserStatistics(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        Map<String, Object> stats = statisticsService.getUserStatistics(userId);
        return R.ok(stats);
    }
}
