package com.campus.donation.module.certificate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.donation.module.certificate.entity.DonationCertificate;
import com.campus.donation.module.certificate.vo.DonationCertificateVO;

import java.util.List;

/**
 * 电子捐赠证明服务
 */
public interface DonationCertificateService extends IService<DonationCertificate> {

    /**
     * 为已完成预约生成证明（已存在则直接返回）
     */
    DonationCertificate ensureCertificateForAppointment(Long appointmentId);

    /**
     * 查询当前捐赠者全部证明
     */
    List<DonationCertificateVO> getMyCertificates(Long donorId);

    /**
     * 根据预约查询证明详情
     */
    DonationCertificateVO getByAppointment(Long appointmentId, Long userId, boolean isAdmin);

    /**
     * 根据证明ID查询详情
     */
    DonationCertificateVO getDetail(Long certificateId, Long userId, boolean isAdmin);

    /**
     * 构建可下载 PDF
     */
    byte[] buildCertificatePdf(DonationCertificateVO certificateVO);
}
