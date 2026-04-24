package com.campus.donation.module.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.donation.common.enums.AppointmentStatus;
import com.campus.donation.common.exception.BusinessException;
import com.campus.donation.module.donation.entity.DonationAppointment;
import com.campus.donation.module.donation.mapper.DonationAppointmentMapper;
import com.campus.donation.module.item.entity.DonationItem;
import com.campus.donation.module.item.mapper.DonationItemMapper;
import com.campus.donation.module.item.service.DonationItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 捐赠旧物服务实现类
 */
@Service
public class DonationItemServiceImpl implements DonationItemService {

    private final DonationItemMapper itemMapper;
    private final DonationAppointmentMapper appointmentMapper;

    // 图片上传路径（实际项目中应配置到application.yml）
    private static final String UPLOAD_DIR = "/uploads/items/";

    public DonationItemServiceImpl(DonationItemMapper itemMapper,
            DonationAppointmentMapper appointmentMapper) {
        this.itemMapper = itemMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    @Transactional
    public void createItemsFromAppointment(Long appointmentId) {
        // 查询预约信息
        DonationAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new BusinessException("预约不存在");
        }

        Long exists = itemMapper.selectCount(new LambdaQueryWrapper<DonationItem>()
                .eq(DonationItem::getAppointmentId, appointmentId)
                .eq(DonationItem::getDeleted, 0));
        if (exists != null && exists > 0) {
            return;
        }

        // 创建旧物记录
        DonationItem item = new DonationItem();
        item.setAppointmentId(appointmentId);
        item.setItemName(resolveItemName(appointment.getItemType()));
        item.setItemType(appointment.getItemType());
        item.setQuantity(appointment.getItemQuantity());
        item.setItemCondition(appointment.getItemCondition());
        item.setDescription(appointment.getItemDesc());
        item.setStatus(0); // 待整理

        itemMapper.insert(item);
    }

    @Override
    public List<DonationItem> getItemsByAppointment(Long appointmentId) {
        return itemMapper.selectByAppointmentId(appointmentId);
    }

    @Override
    public List<DonationItem> getItemsByStatus(Integer status) {
        return itemMapper.selectList(new LambdaQueryWrapper<DonationItem>()
                .eq(DonationItem::getStatus, status)
                .eq(DonationItem::getDeleted, 0)
                .orderByDesc(DonationItem::getCreateTime));
    }

    @Override
    @Transactional
    public void updateItemStatus(Long id, Integer status) {
        DonationItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("旧物不存在");
        }
        if (status == null || !Set.of(0, 1, 2, 3).contains(status)) {
            throw new BusinessException("旧物状态不合法");
        }

        item.setStatus(status);
        itemMapper.updateById(item);

        DonationAppointment appointment = appointmentMapper.selectById(item.getAppointmentId());
        if (appointment != null) {
            if (status == 1 && appointment.getStatus() < AppointmentStatus.SORTING.getCode()) {
                appointment.setStatus(AppointmentStatus.SORTING.getCode());
                appointmentMapper.updateById(appointment);
            } else if (status == 2 && appointment.getStatus() < AppointmentStatus.DOCKED.getCode()) {
                appointment.setStatus(AppointmentStatus.DOCKED.getCode());
                appointmentMapper.updateById(appointment);
            } else if (status == 3 && appointment.getStatus() < AppointmentStatus.COMPLETED.getCode()) {
                appointment.setStatus(AppointmentStatus.COMPLETED.getCode());
                appointmentMapper.updateById(appointment);
            }
        }
    }

    @Override
    @Transactional
    public void updateStorageLocation(Long id, String location) {
        DonationItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("旧物不存在");
        }

        item.setStorageLocation(location);
        itemMapper.updateById(item);
    }

    @Override
    @Transactional
    public String uploadItemImage(Long id, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        DonationItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException("旧物不存在");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID().toString() + extension;

            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 更新图片URL
            String imageUrl = UPLOAD_DIR + filename;
            item.setImageUrl(imageUrl);
            itemMapper.updateById(item);

            return imageUrl;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getItemTypeStatistics() {
        return itemMapper.countByType();
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
}
