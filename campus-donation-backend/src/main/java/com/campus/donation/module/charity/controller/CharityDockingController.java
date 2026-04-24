package com.campus.donation.module.charity.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.donation.common.result.R;
import com.campus.donation.module.charity.dto.CharityDockingDTO;
import com.campus.donation.module.charity.entity.CharityDocking;
import com.campus.donation.module.charity.service.CharityDockingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公益对接控制器
 */
@Tag(name = "公益对接管理")
@RestController
@RequestMapping("/charity")
public class CharityDockingController {

    private final CharityDockingService dockingService;

    public CharityDockingController(CharityDockingService dockingService) {
        this.dockingService = dockingService;
    }

    @Operation(summary = "创建对接申请")
    @PreAuthorize("hasAnyAuthority('CHARITY_OPERATOR', 'ADMIN')")
    @PostMapping("/docking")
    public R<CharityDocking> createDocking(@Valid @RequestBody CharityDockingDTO dto,
            Authentication authentication) {
        Long operatorId = Long.parseLong(authentication.getName());
        CharityDocking docking = dockingService.createDocking(dto, operatorId);
        return R.ok(docking);
    }

    @Operation(summary = "查询对接列表")
    @PreAuthorize("hasAnyAuthority('CHARITY_OPERATOR', 'ADMIN')")
    @GetMapping("/docking/list")
    public R<List<CharityDocking>> getDockingList(@RequestParam(required = false) Integer status,
            Authentication authentication) {
        Long operatorId = Long.parseLong(authentication.getName());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
        List<CharityDocking> list = dockingService.getDockingList(status, operatorId, isAdmin);
        return R.ok(list);
    }

    @Operation(summary = "管理查询对接列表 (管理员)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/docking/admin/list")
    public R<IPage<CharityDocking>> getAdminDockingList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<CharityDocking> page = new Page<>(current, size);
        return R.ok(dockingService.page(page, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharityDocking>()
                .eq(status != null, CharityDocking::getStatus, status)
                .orderByDesc(CharityDocking::getCreateTime)));
    }

    @Operation(summary = "查询对接详情")
    @PreAuthorize("hasAnyAuthority('CHARITY_OPERATOR', 'ADMIN')")
    @GetMapping("/docking/{id}")
    public R<CharityDocking> getDockingDetail(@PathVariable Long id) {
        CharityDocking docking = dockingService.getDockingDetail(id);
        return R.ok(docking);
    }

    @Operation(summary = "上传反馈")
    @PreAuthorize("hasAnyAuthority('CHARITY_OPERATOR', 'ADMIN')")
    @PutMapping("/docking/{id}/feedback")
    public R<String> uploadFeedback(@PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String feedbackText = request.get("feedbackText");
        String feedbackImages = request.get("feedbackImages");
        dockingService.uploadFeedback(id, feedbackText, feedbackImages);
        return R.ok("反馈上传成功");
    }

    @Operation(summary = "完成对接")
    @PreAuthorize("hasAnyAuthority('CHARITY_OPERATOR', 'ADMIN')")
    @PutMapping("/docking/{id}/complete")
    public R<String> completeDocking(@PathVariable Long id) {
        dockingService.completeDocking(id);
        return R.ok("对接完成");
    }

    @Operation(summary = "月度对接统计")
    @GetMapping("/statistics/monthly")
    public R<List<Map<String, Object>>> getMonthlyStatistics() {
        List<Map<String, Object>> stats = dockingService.getMonthlyStatistics();
        return R.ok(stats);
    }

    @Operation(summary = "对接总数")
    @GetMapping("/statistics/total")
    public R<Integer> getTotalCount() {
        Integer count = dockingService.getTotalCount();
        return R.ok(count);
    }
}
