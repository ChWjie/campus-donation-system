package com.campus.donation.module.donation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.donation.common.enums.AppointmentStatus;
import com.campus.donation.common.enums.NoticeType;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.donation.dto.AppointmentDTO;
import com.campus.donation.module.donation.entity.DonationAppointment;
import com.campus.donation.module.donation.mapper.DonationAppointmentMapper;
import com.campus.donation.module.donation.service.DonationAppointmentService;
import com.campus.donation.module.donation.vo.AppointmentVO;
import com.campus.donation.module.item.entity.DonationItem;
import com.campus.donation.module.item.mapper.DonationItemMapper;
import com.campus.donation.module.notification.service.SysNotificationService;
import com.campus.donation.module.station.service.CollectionStationService;
import com.campus.donation.utils.QrCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 捐赠预约服务实现类
 */
@Service
public class DonationAppointmentServiceImpl
        extends ServiceImpl<DonationAppointmentMapper, DonationAppointment>
        implements DonationAppointmentService {

    private static final Logger log = LoggerFactory.getLogger(DonationAppointmentServiceImpl.class);

    private final CollectionStationService stationService;
    private final QrCodeUtil qrCodeUtil;
    private final DonationItemMapper donationItemMapper;
    private final SysNotificationService notificationService;

    public DonationAppointmentServiceImpl(CollectionStationService stationService,
            QrCodeUtil qrCodeUtil,
            DonationItemMapper donationItemMapper,
            SysNotificationService notificationService) {
        this.stationService = stationService;
        this.qrCodeUtil = qrCodeUtil;
        this.donationItemMapper = donationItemMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentVO createAppointment(AppointmentDTO dto, Long donorId) {
        validateCreateParams(dto, donorId);

        // 1. 校验回收点容量
        stationService.checkCapacity(dto.getStationId());

        // 2. 生成预约码
        String code = generateUniqueCode(donorId, dto.getStationId());

        // 3. 创建预约记录
        DonationAppointment appointment = new DonationAppointment();
        appointment.setDonorId(donorId);
        appointment.setAppointCode(code);
        appointment.setItemType(dto.getItemType());
        appointment.setItemQuantity(dto.getItemQuantity());
        appointment.setItemCondition(dto.getItemCondition());
        appointment.setItemDesc(dto.getItemDesc());
        appointment.setStationId(dto.getStationId());
        appointment.setAppointTime(dto.getAppointTime());
        appointment.setStatus(AppointmentStatus.PENDING.getCode());

        save(appointment);

        // 4. 更新回收点存量
        stationService.updateCurrentNum(dto.getStationId(), 1);

        // 5. 生成二维码
        String qrCodeBase64 = qrCodeUtil.generateAppointmentQrCode(code);

        // 6. 构造返回VO
        AppointmentVO vo = new AppointmentVO();
        vo.setId(appointment.getId());
        vo.setAppointCode(code);
        vo.setQrCodeBase64(qrCodeBase64);
        vo.setItemType(appointment.getItemType());
        vo.setItemQuantity(appointment.getItemQuantity());
        vo.setItemCondition(appointment.getItemCondition());
        vo.setItemDesc(appointment.getItemDesc());
        vo.setStationId(appointment.getStationId());
        vo.setAppointTime(appointment.getAppointTime());
        vo.setStatus(appointment.getStatus());
        vo.setStatusDesc(AppointmentStatus.getDescByCode(appointment.getStatus()));
        vo.setCreateTime(appointment.getCreateTime());

        notificationService.sendNotice(
                donorId,
                "预约创建成功",
                "您的捐赠预约已创建成功，预约码为：" + code + "，请按时前往回收点完成交付。",
                NoticeType.APPOINT_REMINDER.getCode());

        log.info("创建捐赠预约成功: donorId={}, code={}", donorId, code);
        return vo;
    }

    @Override
    public List<AppointmentVO> getMyAppointments(Long donorId) {
        List<AppointmentVO> list = baseMapper.selectMyAppointments(donorId);
        // 填充状态描述
        list.forEach(vo -> vo.setStatusDesc(AppointmentStatus.getDescByCode(vo.getStatus())));
        return list;
    }

    @Override
    public AppointmentVO getAppointmentDetail(Long id) {
        AppointmentVO vo = baseMapper.selectAppointmentDetail(id);
        if (vo == null) {
            throw new BusinessException("预约不存在");
        }
        vo.setStatusDesc(AppointmentStatus.getDescByCode(vo.getStatus()));
        // 生成二维码
        vo.setQrCodeBase64(qrCodeUtil.generateAppointmentQrCode(vo.getAppointCode()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void volunteerConfirm(String appointCode, Long volunteerId, String itemType, String itemCondition) {
        // 查询预约
        AppointmentVO vo = baseMapper.selectByCode(appointCode);
        if (vo == null) {
            throw new BusinessException("预约码无效");
        }
        if (!vo.getStatus().equals(AppointmentStatus.PENDING.getCode())) {
            throw new BusinessException("该预约已被处理");
        }

        DonationAppointment appointment = getById(vo.getId());
        appointment.setStatus(AppointmentStatus.RECEIVED.getCode());
        appointment.setVolunteerId(volunteerId);
        
        if (itemType != null && !itemType.isEmpty()) {
            appointment.setItemType(itemType);
        }
        if (itemCondition != null && !itemCondition.isEmpty()) {
            appointment.setItemCondition(itemCondition);
        }

        updateById(appointment);

        long existsItemCount = donationItemMapper.selectCount(
                new LambdaQueryWrapper<DonationItem>()
                        .eq(DonationItem::getAppointmentId, appointment.getId())
                        .eq(DonationItem::getDeleted, 0));

        if (existsItemCount == 0) {
            DonationItem item = new DonationItem();
            item.setAppointmentId(appointment.getId());
            item.setItemName(resolveItemName(appointment.getItemType()));
            item.setItemType(appointment.getItemType());
            item.setQuantity(appointment.getItemQuantity());
            item.setItemCondition(appointment.getItemCondition());
            item.setDescription(appointment.getItemDesc());
            item.setStatus(0);
            donationItemMapper.insert(item);
        }

        notificationService.sendNotice(
                appointment.getDonorId(),
                "旧物已接收",
                "您的预约（" + appointment.getAppointCode() + "）已由志愿者确认接收。",
                NoticeType.RECEIVE_CONFIRM.getCode());

        log.info("志愿者确认接收: appointCode={}, volunteerId={}", appointCode, volunteerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        DonationAppointment appointment = getById(id);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }
        if (status == null) {
            throw new BusinessException("状态不能为空");
        }
        AppointmentStatus targetStatus = parseStatus(status);
        AppointmentStatus currentStatus = parseStatus(appointment.getStatus());
        if (targetStatus == currentStatus) {
            return;
        }
        validateStatusTransition(currentStatus, targetStatus);

        appointment.setStatus(targetStatus.getCode());
        updateById(appointment);

        if (targetStatus == AppointmentStatus.CANCELLED || targetStatus == AppointmentStatus.COMPLETED) {
            stationService.updateCurrentNum(appointment.getStationId(), -1);
        }

        notificationService.sendNotice(
                appointment.getDonorId(),
                "预约状态更新",
                "您的预约（" + appointment.getAppointCode() + "）状态已更新为：" + targetStatus.getDesc(),
                NoticeType.RECEIVE_CONFIRM.getCode());

        log.info("更新预约状态: id={}, status={}", id, status);
    }

    @Override
    public List<AppointmentVO> getVolunteerConfirms(Long volunteerId) {
        List<AppointmentVO> list = baseMapper.selectVolunteerConfirms(volunteerId);
        list.forEach(vo -> vo.setStatusDesc(AppointmentStatus.getDescByCode(vo.getStatus())));
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long appointmentId, Long donorId) {
        DonationAppointment appointment = lambdaQuery()
                .eq(DonationAppointment::getId, appointmentId)
                .eq(DonationAppointment::getDonorId, donorId)
                .eq(DonationAppointment::getDeleted, 0)
                .one();

        if (appointment == null) {
            throw new BusinessException("预约不存在或无权限操作");
        }
        if (!AppointmentStatus.PENDING.getCode().equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许取消预约");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED.getCode());
        updateById(appointment);
        stationService.updateCurrentNum(appointment.getStationId(), -1);

        notificationService.sendNotice(
                donorId,
                "预约已取消",
                "您已取消预约（" + appointment.getAppointCode() + "），如需继续捐赠请重新预约。",
                NoticeType.APPOINT_REMINDER.getCode());
    }

    private void validateCreateParams(AppointmentDTO dto, Long donorId) {
        if (!StringUtils.hasText(dto.getItemType())) {
            throw new BusinessException("旧物类型不能为空");
        }
        if (dto.getItemQuantity() == null || dto.getItemQuantity() <= 0) {
            throw new BusinessException("旧物数量必须大于0");
        }
        if (!StringUtils.hasText(dto.getItemCondition())) {
            throw new BusinessException("旧物完好度不能为空");
        }
        if (!StringUtils.hasText(dto.getItemDesc())) {
            throw new BusinessException("旧物描述不能为空");
        }
        if (dto.getItemDesc().length() > 500) {
            throw new BusinessException("旧物描述长度不能超过500");
        }
        if (dto.getStationId() == null) {
            throw new BusinessException("回收点不能为空");
        }
        if (dto.getAppointTime() == null || dto.getAppointTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("预约时间必须为未来时间");
        }

        long duplicateCount = lambdaQuery()
                .eq(DonationAppointment::getDonorId, donorId)
                .eq(DonationAppointment::getStationId, dto.getStationId())
                .eq(DonationAppointment::getAppointTime, dto.getAppointTime())
                .in(DonationAppointment::getStatus, Arrays.asList(
                        AppointmentStatus.PENDING.getCode(),
                        AppointmentStatus.RECEIVED.getCode(),
                        AppointmentStatus.SORTING.getCode()))
                .count();

        if (duplicateCount > 0) {
            throw new BusinessException("相同时间和回收点已存在预约，请勿重复提交");
        }
    }

    private AppointmentStatus parseStatus(Integer code) {
        try {
            return AppointmentStatus.fromCode(code);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("非法的预约状态");
        }
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus target) {
        Set<AppointmentStatus> allowed = switch (current) {
            case PENDING -> new HashSet<>(Arrays.asList(AppointmentStatus.RECEIVED, AppointmentStatus.CANCELLED));
            case RECEIVED -> Set.of(AppointmentStatus.SORTING);
            case SORTING -> Set.of(AppointmentStatus.DOCKED);
            case DOCKED -> Set.of(AppointmentStatus.COMPLETED);
            case COMPLETED, CANCELLED -> Set.of();
        };
        if (!allowed.contains(target)) {
            throw new BusinessException("预约状态流转不合法");
        }
    }

    private String resolveItemName(String itemType) {
        return switch (itemType) {
            case "book" -> "图书资料";
            case "clothing" -> "衣物";
            case "electronics" -> "电子产品";
            case "daily" -> "生活用品";
            default -> "公益旧物";
        };
    }

    private String generateUniqueCode(Long donorId, Long stationId) {
        for (int i = 0; i < 5; i++) {
            String code = qrCodeUtil.generateCode(donorId, stationId);
            long count = lambdaQuery().eq(DonationAppointment::getAppointCode, code).count();
            if (count == 0) {
                return code;
            }
        }
        throw new BusinessException("预约码生成失败，请稍后重试");
    }
}
