package com.campus.donation.module.item.controller;

import com.campus.donation.common.result.R;
import com.campus.donation.module.item.entity.DonationItem;
import com.campus.donation.module.item.service.DonationItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 捐赠旧物控制器
 */
@Tag(name = "旧物管理")
@RestController
@RequestMapping("/item")
public class DonationItemController {

    private final DonationItemService itemService;

    public DonationItemController(DonationItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "从预约创建旧物记录")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'ADMIN')")
    @PostMapping("/create/{appointmentId}")
    public R<String> createFromAppointment(@PathVariable Long appointmentId) {
        itemService.createItemsFromAppointment(appointmentId);
        return R.ok("创建成功");
    }

    @Operation(summary = "根据预约ID查询旧物")
    @GetMapping("/appointment/{appointmentId}")
    public R<List<DonationItem>> getByAppointment(@PathVariable Long appointmentId) {
        List<DonationItem> items = itemService.getItemsByAppointment(appointmentId);
        return R.ok(items);
    }

    @Operation(summary = "根据状态查询旧物列表")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'CHARITY_OPERATOR', 'ADMIN')")
    @GetMapping("/list")
    public R<List<DonationItem>> getByStatus(@RequestParam(required = false) Integer status) {
        List<DonationItem> items = status != null ? itemService.getItemsByStatus(status)
                : itemService.getItemsByStatus(0); // 默认查询待整理
        return R.ok(items);
    }

    @Operation(summary = "更新旧物状态")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'CHARITY_OPERATOR', 'ADMIN')")
    @PutMapping("/{id}/status")
    public R<String> updateStatus(@PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        itemService.updateItemStatus(id, status);
        return R.ok("更新成功");
    }

    @Operation(summary = "更新仓库位置")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'ADMIN')")
    @PutMapping("/{id}/storage")
    public R<String> updateStorage(@PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String location = request.get("location");
        itemService.updateStorageLocation(id, location);
        return R.ok("更新成功");
    }

    @Operation(summary = "上传旧物图片")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'ADMIN')")
    @PostMapping("/{id}/image")
    public R<String> uploadImage(@PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = itemService.uploadItemImage(id, file);
        return R.ok(imageUrl);
    }

    @Operation(summary = "旧物类型统计")
    @GetMapping("/statistics/type")
    public R<List<Map<String, Object>>> getTypeStatistics() {
        List<Map<String, Object>> stats = itemService.getItemTypeStatistics();
        return R.ok(stats);
    }
}
