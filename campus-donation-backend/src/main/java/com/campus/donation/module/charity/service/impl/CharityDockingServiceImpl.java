package com.campus.donation.module.charity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.donation.common.enums.AppointmentStatus;
import com.campus.donation.common.enums.NoticeType;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.charity.dto.CharityDockingDTO;
import com.campus.donation.module.charity.entity.CharityDocking;
import com.campus.donation.module.charity.mapper.CharityDockingMapper;
import com.campus.donation.module.charity.service.CharityDockingService;
import com.campus.donation.module.certificate.service.DonationCertificateService;
import com.campus.donation.module.donation.entity.DonationAppointment;
import com.campus.donation.module.donation.mapper.DonationAppointmentMapper;
import com.campus.donation.module.item.entity.DonationItem;
import com.campus.donation.module.item.mapper.DonationItemMapper;
import com.campus.donation.module.notification.service.SysNotificationService;
import com.campus.donation.module.station.service.CollectionStationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 公益对接服务实现类
 */
@Service
public class CharityDockingServiceImpl extends ServiceImpl<CharityDockingMapper, CharityDocking> implements CharityDockingService {

    private final CharityDockingMapper dockingMapper;
    private final DonationItemMapper itemMapper;
    private final DonationAppointmentMapper appointmentMapper;
    private final SysNotificationService notificationService;
    private final DonationCertificateService certificateService;
    private final CollectionStationService stationService;

    public CharityDockingServiceImpl(CharityDockingMapper dockingMapper,
            DonationItemMapper itemMapper,
            DonationAppointmentMapper appointmentMapper,
            SysNotificationService notificationService,
            DonationCertificateService certificateService,
            CollectionStationService stationService) {
        this.dockingMapper = dockingMapper;
        this.itemMapper = itemMapper;
        this.appointmentMapper = appointmentMapper;
        this.notificationService = notificationService;
        this.certificateService = certificateService;
        this.stationService = stationService;
    }

    @Override
    @Transactional
    public CharityDocking createDocking(CharityDockingDTO dto, Long operatorId) {
        if (!StringUtils.hasText(dto.getCharityName())) {
            throw new BusinessException("受赠组织名称不能为空");
        }
        if (!StringUtils.hasText(dto.getCharityContact())) {
            throw new BusinessException("受赠方联系方式不能为空");
        }

        List<Long> itemIds = parseItemIds(dto.getItemIds());
        if (itemIds.isEmpty()) {
            throw new BusinessException("请至少选择一个待对接旧物");
        }

        List<DonationItem> items = itemMapper.selectBatchIds(itemIds);
        if (items.size() != itemIds.size()) {
            throw new BusinessException("存在无效旧物记录，请刷新后重试");
        }
        for (DonationItem item : items) {
            if (item.getStatus() != null && item.getStatus() == 2) {
                throw new BusinessException("存在已对接旧物，无法重复发起对接");
            }
        }

        // 生成对接编号
        String dockingNo = generateDockingNo();

        CharityDocking docking = new CharityDocking();
        docking.setDockingNo(dockingNo);
        docking.setOperatorId(operatorId);
        docking.setCharityName(dto.getCharityName());
        docking.setCharityContact(dto.getCharityContact());
        docking.setPurpose(dto.getPurpose());
        docking.setItemIds(joinIds(itemIds));
        int autoCount = items.stream().mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity()).sum();
        docking.setItemCount(dto.getItemCount() != null && dto.getItemCount() > 0 ? dto.getItemCount() : autoCount);
        docking.setStatus(0); // 申请中
        docking.setDockingTime(null);

        dockingMapper.insert(docking);

        Set<Long> appointmentIds = new LinkedHashSet<>();
        for (DonationItem item : items) {
            item.setStatus(2);
            itemMapper.updateById(item);
            appointmentIds.add(item.getAppointmentId());
        }

        List<DonationAppointment> appointments = appointmentIds.isEmpty()
                ? List.of()
                : appointmentMapper.selectBatchIds(new ArrayList<>(appointmentIds));
        for (DonationAppointment appointment : appointments) {
            if (!AppointmentStatus.CANCELLED.getCode().equals(appointment.getStatus())
                    && appointment.getStatus() < AppointmentStatus.DOCKED.getCode()) {
                appointment.setStatus(AppointmentStatus.DOCKED.getCode());
                appointmentMapper.updateById(appointment);
            }
            notificationService.sendNotice(
                    appointment.getDonorId(),
                    "旧物已进入公益对接",
                    "您的旧物已匹配至公益组织“" + dto.getCharityName() + "”，对接单号：" + dockingNo + "。",
                    NoticeType.DOCKING_COMPLETE.getCode());
        }

        return docking;
    }

    @Override
    public List<CharityDocking> getDockingList(Integer status, Long operatorId, boolean isAdmin) {
        return lambdaQuery()
                .eq(status != null, CharityDocking::getStatus, status)
                .eq(!isAdmin, CharityDocking::getOperatorId, operatorId)
                .orderByDesc(CharityDocking::getCreateTime)
                .list();
    }

    @Override
    public CharityDocking getDockingDetail(Long id) {
        CharityDocking docking = dockingMapper.selectById(id);
        if (docking == null) {
            throw new BusinessException("对接记录不存在");
        }
        return docking;
    }

    @Override
    @Transactional
    public void uploadFeedback(Long id, String feedbackText, String feedbackImages) {
        CharityDocking docking = dockingMapper.selectById(id);
        if (docking == null) {
            throw new BusinessException("对接记录不存在");
        }
        if (!StringUtils.hasText(feedbackText) && !StringUtils.hasText(feedbackImages)) {
            throw new BusinessException("请至少填写文字反馈或图片反馈");
        }

        docking.setFeedbackText(feedbackText);
        docking.setFeedbackImages(feedbackImages);
        docking.setStatus(2); // 已反馈
        if (docking.getDockingTime() == null) {
            docking.setDockingTime(LocalDateTime.now());
        }
        dockingMapper.updateById(docking);

        List<Long> itemIds = parseItemIds(docking.getItemIds());
        if (!itemIds.isEmpty()) {
            List<DonationItem> items = itemMapper.selectBatchIds(itemIds);
            Set<Long> appointmentIds = new LinkedHashSet<>();
            for (DonationItem item : items) {
                item.setStatus(3);
                itemMapper.updateById(item);
                appointmentIds.add(item.getAppointmentId());
            }

            List<DonationAppointment> appointments = appointmentMapper.selectBatchIds(new ArrayList<>(appointmentIds));
            for (DonationAppointment appointment : appointments) {
                if (!AppointmentStatus.CANCELLED.getCode().equals(appointment.getStatus())) {
                    appointment.setStatus(AppointmentStatus.COMPLETED.getCode());
                    appointmentMapper.updateById(appointment);
                    try {
                        stationService.updateCurrentNum(appointment.getStationId(), -1);
                    } catch (Exception ignored) {
                        // 站点当前存量可能已在其他流程更新，此处不阻断反馈流程
                    }
                    certificateService.ensureCertificateForAppointment(appointment.getId());
                    notificationService.sendNotice(
                            appointment.getDonorId(),
                            "公益反馈已更新",
                            "您捐赠的旧物已完成反馈，感谢您的爱心支持！",
                            NoticeType.CHARITY_FEEDBACK.getCode());
                }
            }
        }
    }

    @Override
    @Transactional
    public void completeDocking(Long id) {
        CharityDocking docking = dockingMapper.selectById(id);
        if (docking == null) {
            throw new BusinessException("对接记录不存在");
        }
        if (docking.getStatus() != null && docking.getStatus() == 2) {
            return;
        }

        docking.setStatus(1); // 已对接
        docking.setDockingTime(LocalDateTime.now());
        dockingMapper.updateById(docking);

        List<Long> itemIds = parseItemIds(docking.getItemIds());
        if (!itemIds.isEmpty()) {
            List<DonationItem> items = itemMapper.selectBatchIds(itemIds);
            Set<Long> appointmentIds = new LinkedHashSet<>();
            for (DonationItem item : items) {
                appointmentIds.add(item.getAppointmentId());
            }
            List<DonationAppointment> appointments = appointmentMapper.selectBatchIds(new ArrayList<>(appointmentIds));
            for (DonationAppointment appointment : appointments) {
                notificationService.sendNotice(
                        appointment.getDonorId(),
                        "旧物已完成对接",
                        "您的旧物已完成公益对接，后续将更新受赠反馈。",
                        NoticeType.DOCKING_COMPLETE.getCode());
            }
        }
    }

    @Override
    public List<Map<String, Object>> getMonthlyStatistics() {
        return dockingMapper.countMonthlyDocking();
    }

    @Override
    public Integer getTotalCount() {
        return dockingMapper.countTotal();
    }

    /**
     * 生成对接编号
     * 格式：DOCK-YYYYMMDD-序号
     */
    private String generateDockingNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int count = dockingMapper.countTotal() + 1;
        return String.format("DOCK-%s-%04d", dateStr, count);
    }

    private List<Long> parseItemIds(String itemIds) {
        if (!StringUtils.hasText(itemIds)) {
            return List.of();
        }
        Set<Long> uniqueIds = new LinkedHashSet<>();
        String[] parts = itemIds.split(",");
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            try {
                uniqueIds.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException ex) {
                throw new BusinessException("旧物ID格式不正确");
            }
        }
        return new ArrayList<>(uniqueIds);
    }

    private String joinIds(List<Long> ids) {
        return ids.stream().map(String::valueOf).reduce((left, right) -> left + "," + right).orElse("");
    }
}
