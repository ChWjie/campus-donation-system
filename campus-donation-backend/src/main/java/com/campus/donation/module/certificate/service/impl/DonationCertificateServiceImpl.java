package com.campus.donation.module.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.donation.common.enums.AppointmentStatus;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.certificate.entity.DonationCertificate;
import com.campus.donation.module.certificate.mapper.DonationCertificateMapper;
import com.campus.donation.module.certificate.service.DonationCertificateService;
import com.campus.donation.module.certificate.vo.DonationCertificateVO;
import com.campus.donation.module.donation.entity.DonationAppointment;
import com.campus.donation.module.donation.mapper.DonationAppointmentMapper;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 电子捐赠证明服务实现
 */
@Service
public class DonationCertificateServiceImpl extends ServiceImpl<DonationCertificateMapper, DonationCertificate>
        implements DonationCertificateService {

    private final DonationAppointmentMapper appointmentMapper;

    public DonationCertificateServiceImpl(DonationAppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DonationCertificate ensureCertificateForAppointment(Long appointmentId) {
        DonationCertificate exists = lambdaQuery()
                .eq(DonationCertificate::getAppointmentId, appointmentId)
                .eq(DonationCertificate::getDeleted, 0)
                .one();
        if (exists != null) {
            return exists;
        }

        DonationAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在，无法生成证明");
        }
        if (!AppointmentStatus.COMPLETED.getCode().equals(appointment.getStatus())) {
            throw new BusinessException("预约未完成，暂不可生成证明");
        }

        DonationCertificate certificate = new DonationCertificate();
        certificate.setDonorId(appointment.getDonorId());
        certificate.setAppointmentId(appointment.getId());
        certificate.setCertNo(generateCertificateNo());
        certificate.setIssueTime(LocalDateTime.now());
        save(certificate);

        certificate.setCertUrl("/api/certificate/download/" + certificate.getId());
        updateById(certificate);
        return certificate;
    }

    @Override
    public List<DonationCertificateVO> getMyCertificates(Long donorId) {
        return baseMapper.selectMyCertificates(donorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DonationCertificateVO getByAppointment(Long appointmentId, Long userId, boolean isAdmin) {
        DonationAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        if (!isAdmin && !appointment.getDonorId().equals(userId)) {
            throw new BusinessException(403, "无权访问该预约证明");
        }

        DonationCertificateVO detail = baseMapper.selectByAppointmentId(appointmentId);
        if (detail == null) {
            DonationCertificate certificate = ensureCertificateForAppointment(appointmentId);
            detail = baseMapper.selectDetailById(certificate.getId());
        }
        if (detail == null) {
            throw new BusinessException("证明不存在");
        }
        verifyPermission(detail, userId, isAdmin);
        return detail;
    }

    @Override
    public DonationCertificateVO getDetail(Long certificateId, Long userId, boolean isAdmin) {
        DonationCertificateVO detail = baseMapper.selectDetailById(certificateId);
        if (detail == null) {
            throw new BusinessException("证明不存在");
        }
        verifyPermission(detail, userId, isAdmin);
        return detail;
    }

    @Override
    public byte[] buildCertificatePdf(DonationCertificateVO certificateVO) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");

            document.add(new Paragraph("校园旧物公益捐赠证明").setFont(font).setFontSize(22));
            document.add(new Paragraph("证书编号：" + certificateVO.getCertNo()).setFont(font).setFontSize(12));
            document.add(new Paragraph(" ").setFont(font));
            document.add(new Paragraph("捐赠者：" + defaultValue(certificateVO.getDonorName())).setFont(font));
            document.add(new Paragraph("预约码：" + defaultValue(certificateVO.getAppointCode())).setFont(font));
            document.add(new Paragraph("物品类型：" + defaultValue(certificateVO.getItemType())).setFont(font));
            document.add(new Paragraph("捐赠数量：" + (certificateVO.getItemQuantity() == null ? 0 : certificateVO.getItemQuantity()) + " 件")
                    .setFont(font));
            document.add(new Paragraph("物品说明：" + defaultValue(certificateVO.getItemDesc())).setFont(font));
            document.add(new Paragraph("预约时间：" + formatTime(certificateVO.getAppointTime())).setFont(font));
            document.add(new Paragraph("回收点：" + defaultValue(certificateVO.getStationName())).setFont(font));
            document.add(new Paragraph("签发时间：" + formatTime(certificateVO.getIssueTime())).setFont(font));
            document.add(new Paragraph(" ").setFont(font));
            document.add(new Paragraph("感谢您为校园公益与资源循环做出的贡献。").setFont(font));
            document.add(new Paragraph("校园旧物公益捐赠回收管理平台").setFont(font));
            document.close();

            return outputStream.toByteArray();
        } catch (Exception ex) {
            throw new BusinessException("证明文件生成失败");
        }
    }

    private void verifyPermission(DonationCertificateVO detail, Long userId, boolean isAdmin) {
        if (!isAdmin && !detail.getDonorId().equals(userId)) {
            throw new BusinessException(403, "无权访问该证明");
        }
    }

    private String generateCertificateNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = ThreadLocalRandom.current().nextInt(100000, 999999);
        return "CERT-" + date + "-" + random;
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) {
            return "-";
        }
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String defaultValue(String value) {
        return value == null ? "-" : value;
    }
}
