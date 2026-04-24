package com.campus.donation.module.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.donation.module.item.entity.DonationItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 捐赠旧物Mapper接口
 */
@Mapper
public interface DonationItemMapper extends BaseMapper<DonationItem> {

    /**
     * 根据预约ID查询旧物列表
     */
    @Select("SELECT * FROM donation_item WHERE appointment_id = #{appointmentId} AND deleted = 0")
    List<DonationItem> selectByAppointmentId(Long appointmentId);

    /**
     * 根据状态查询旧物列表
     */
    @Select("SELECT * FROM donation_item WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<DonationItem> selectByStatus(Integer status);

    /**
     * 统计各类型旧物数量
     */
    @Select("SELECT item_type as type, SUM(quantity) as count FROM donation_item " +
            "WHERE deleted = 0 GROUP BY item_type")
    List<java.util.Map<String, Object>> countByType();
}
