package com.campus.donation.module.charity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.donation.common.result.R;
import com.campus.donation.module.charity.entity.CharityQualification;
import com.campus.donation.module.charity.service.CharityQualificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公益资质相关控制器
 */
@Tag(name = "公益资质审核")
@RestController
@RequestMapping("/charity/qualification")
public class CharityQualificationController {

    private final CharityQualificationService qualificationService;

    public CharityQualificationController(CharityQualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    @Operation(summary = "提交资质申请")
    @PostMapping("/submit")
    public R<String> submit(@RequestBody CharityQualification qualification, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        qualification.setUserId(userId);
        qualificationService.submit(qualification);
        return R.ok("提交成功，请等待管理员审核");
    }

    @Operation(summary = "分页查询资质列表 (管理员)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/page")
    public R<Page<CharityQualification>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return R.ok(qualificationService.getQualificationPage(current, size, status));
    }

    @Operation(summary = "审核资质 (管理员)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/audit")
    public R<String> audit(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer status = (Integer) request.get("status");
        String reason = (String) request.get("reason");
        qualificationService.audit(id, status, reason);
        return R.ok("审核完成");
    }

    @Operation(summary = "获取当前用户的资质状态")
    @GetMapping("/status")
    public R<CharityQualification> getMyStatus(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        CharityQualification qualification = qualificationService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CharityQualification>()
                        .eq(CharityQualification::getUserId, userId)
                        .orderByDesc(CharityQualification::getCreateTime)
                        .last("limit 1")
        );
        return R.ok(qualification);
    }

    @Operation(summary = "获取所有已审核通过的公益组织")
    @GetMapping("/verified")
    public R<java.util.List<CharityQualification>> getVerifiedList() {
        return R.ok(qualificationService.getVerifiedList());
    }
}
