package com.campus.donation.module.certificate.controller;

import com.campus.donation.common.result.R;
import com.campus.donation.module.certificate.entity.DonationCertificate;
import com.campus.donation.module.certificate.service.DonationCertificateService;
import com.campus.donation.module.certificate.vo.DonationCertificateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 电子证明控制器
 */
@Tag(name = "电子捐赠证明")
@RestController
@RequestMapping("/certificate")
public class DonationCertificateController {

    private final DonationCertificateService certificateService;

    public DonationCertificateController(DonationCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @Operation(summary = "查询我的电子证明")
    @PreAuthorize("hasAuthority('DONOR')")
    @GetMapping("/my")
    public R<List<DonationCertificateVO>> getMyCertificates(Authentication authentication) {
        Long donorId = Long.parseLong(authentication.getName());
        return R.ok(certificateService.getMyCertificates(donorId));
    }

    @Operation(summary = "按预约查询证明")
    @PreAuthorize("hasAnyAuthority('DONOR', 'ADMIN')")
    @GetMapping("/appointment/{appointmentId}")
    public R<DonationCertificateVO> getByAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        return R.ok(certificateService.getByAppointment(appointmentId, userId, isAdmin));
    }

    @Operation(summary = "手动生成证明")
    @PreAuthorize("hasAnyAuthority('DONOR', 'ADMIN')")
    @PostMapping("/generate/{appointmentId}")
    public R<DonationCertificate> generate(@PathVariable Long appointmentId) {
        return R.ok(certificateService.ensureCertificateForAppointment(appointmentId));
    }

    @Operation(summary = "下载 PDF 证明")
    @PreAuthorize("hasAnyAuthority('DONOR', 'ADMIN')")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        boolean isAdmin = isAdmin(authentication);
        DonationCertificateVO detail = certificateService.getDetail(id, userId, isAdmin);
        byte[] content = certificateService.buildCertificatePdf(detail);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("donation-certificate-" + detail.getCertNo() + ".pdf").build());

        return ResponseEntity.ok().headers(headers).body(content);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
    }
}
