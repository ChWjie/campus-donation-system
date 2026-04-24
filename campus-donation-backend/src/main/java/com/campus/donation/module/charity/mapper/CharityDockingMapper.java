package com.campus.donation.module.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.donation.module.charity.entity.CharityDocking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 公益对接Mapper接口
 */
@Mapper
public interface CharityDockingMapper extends BaseMapper<CharityDocking> {

    /**
     * 根据状态查询对接列表
     */
    @Select("SELECT * FROM charity_docking WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<CharityDocking> selectByStatus(Integer status);

    /**
     * 根据对接员ID查询对接列表
     */
    @Select("SELECT * FROM charity_docking WHERE operator_id = #{operatorId} AND deleted = 0 ORDER BY create_time DESC")
    List<CharityDocking> selectByOperator(Long operatorId);

    /**
     * 统计月度对接次数
     */
    @Select("SELECT DATE_FORMAT(docking_time, '%Y-%m') as month, COUNT(*) as count " +
            "FROM charity_docking WHERE deleted = 0 AND docking_time IS NOT NULL " +
            "GROUP BY DATE_FORMAT(docking_time, '%Y-%m') ORDER BY month DESC LIMIT 12")
    List<Map<String, Object>> countMonthlyDocking();

    /**
     * 统计对接总数
     */
    @Select("SELECT COUNT(*) FROM charity_docking WHERE deleted = 0")
    Integer countTotal();
}
