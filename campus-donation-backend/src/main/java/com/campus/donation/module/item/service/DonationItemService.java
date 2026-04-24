package com.campus.donation.module.item.service;

import com.campus.donation.module.item.entity.DonationItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 捐赠旧物服务接口
 */
public interface DonationItemService {

    /**
     * 从预约创建旧物记录
     */
    void createItemsFromAppointment(Long appointmentId);

    /**
     * 根据预约ID查询旧物列表
     */
    List<DonationItem> getItemsByAppointment(Long appointmentId);

    /**
     * 根据状态查询旧物列表
     */
    List<DonationItem> getItemsByStatus(Integer status);

    /**
     * 更新旧物状态
     */
    void updateItemStatus(Long id, Integer status);

    /**
     * 更新仓库位置
     */
    void updateStorageLocation(Long id, String location);

    /**
     * 上传旧物图片
     */
    String uploadItemImage(Long id, MultipartFile file);

    /**
     * 统计各类型旧物数量
     */
    List<Map<String, Object>> getItemTypeStatistics();
}
