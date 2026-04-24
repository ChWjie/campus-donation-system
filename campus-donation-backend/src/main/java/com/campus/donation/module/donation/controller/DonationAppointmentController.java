package com.campus.donation.module.donation.controller;

import com.campus.donation.common.result.R;
import com.campus.donation.module.donation.dto.AppointmentDTO;
import com.campus.donation.module.donation.service.DonationAppointmentService;
import com.campus.donation.module.donation.vo.AppointmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 捐赠预约控制器
 */
@Tag(name = "捐赠预约管理")
@RestController
@RequestMapping("/donation")
public class DonationAppointmentController {

    private final DonationAppointmentService appointmentService;

    public DonationAppointmentController(DonationAppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "创建捐赠预约")
    @PreAuthorize("hasAuthority('DONOR')")
    @PostMapping("/appointment")
    public R<AppointmentVO> createAppointment(@Valid @RequestBody AppointmentDTO dto,
            Authentication authentication) {
        Long donorId = Long.parseLong(authentication.getName());
        AppointmentVO vo = appointmentService.createAppointment(dto, donorId);
        return R.ok(vo);
    }

    @Operation(summary = "查询我的预约列表")
    @PreAuthorize("hasAuthority('DONOR')")
    @GetMapping("/appointment/my")
    public R<List<AppointmentVO>> getMyAppointments(Authentication authentication) {
        Long donorId = Long.parseLong(authentication.getName());
        List<AppointmentVO> list = appointmentService.getMyAppointments(donorId);
        return R.ok(list);
    }

    @Operation(summary = "查询预约详情")
    @GetMapping("/appointment/{id}")
    public R<AppointmentVO> getAppointmentDetail(@PathVariable Long id) {
        AppointmentVO vo = appointmentService.getAppointmentDetail(id);
        return R.ok(vo);
    }

    @Operation(summary = "志愿者扫码确认接收")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'ADMIN')")
    @PostMapping("/confirm/{code}")
    public R<String> volunteerConfirm(@PathVariable String code,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {
        Long volunteerId = Long.parseLong(authentication.getName());
        String itemType = request != null ? request.get("itemType") : null;
        String itemCondition = request != null ? request.get("itemCondition") : null;
        appointmentService.volunteerConfirm(code, volunteerId, itemType, itemCondition);
        return R.ok("确认接收成功");
    }

    @Operation(summary = "查询我的确认记录(志愿者)")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'ADMIN')")
    @GetMapping("/appointment/volunteer/confirms")
    public R<List<AppointmentVO>> getVolunteerConfirms(Authentication authentication) {
        Long volunteerId = Long.parseLong(authentication.getName());
        List<AppointmentVO> list = appointmentService.getVolunteerConfirms(volunteerId);
        return R.ok(list);
    }

    @Operation(summary = "更新预约状态")
    @PreAuthorize("hasAnyAuthority('VOLUNTEER', 'CHARITY_OPERATOR', 'ADMIN')")
    @PutMapping("/appointment/{id}/status")
    public R<String> updateStatus(@PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        appointmentService.updateStatus(id, status);
        return R.ok("更新成功");
    }

    @Operation(summary = "取消预约")
    @PreAuthorize("hasAuthority('DONOR')")
    @PutMapping("/appointment/{id}/cancel")
    public R<String> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        Long donorId = Long.parseLong(authentication.getName());
        appointmentService.cancelAppointment(id, donorId);
        return R.ok("取消成功");
    }
}
