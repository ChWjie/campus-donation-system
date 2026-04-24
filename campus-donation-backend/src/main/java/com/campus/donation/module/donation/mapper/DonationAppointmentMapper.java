package com.campus.donation.module.donation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.donation.module.donation.entity.DonationAppointment;
import com.campus.donation.module.donation.vo.AppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 捐赠预约 Mapper 接口
 */
@Mapper
public interface DonationAppointmentMapper extends BaseMapper<DonationAppointment> {

        /**
         * 查询我的预约列表（含回收点名称）
         */
        @Select("SELECT a.*, s.name as stationName " +
                        "FROM donation_appointment a " +
                        "LEFT JOIN collection_station s ON a.station_id = s.id " +
                        "WHERE a.deleted = 0 AND a.donor_id = #{donorId} " +
                        "ORDER BY a.create_time DESC")
        List<AppointmentVO> selectMyAppointments(@Param("donorId") Long donorId);

        /**
         * 查询预约详情（含捐赠者、志愿者、回收点信息）
         */
        @Select("SELECT a.*, " +
                        "u1.real_name as donorName, " +
                        "u2.real_name as volunteerName, " +
                        "s.name as stationName " +
                        "FROM donation_appointment a " +
                        "LEFT JOIN sys_user u1 ON a.donor_id = u1.id " +
                        "LEFT JOIN sys_user u2 ON a.volunteer_id = u2.id " +
                        "LEFT JOIN collection_station s ON a.station_id = s.id " +
                        "WHERE a.deleted = 0 AND a.id = #{id}")
        AppointmentVO selectAppointmentDetail(@Param("id") Long id);

        /**
         * 根据预约码查询预约详情
         */
        @Select("SELECT a.*, " +
                        "u1.real_name as donorName, " +
                        "s.name as stationName " +
                        "FROM donation_appointment a " +
                        "LEFT JOIN sys_user u1 ON a.donor_id = u1.id " +
                        "LEFT JOIN collection_station s ON a.station_id = s.id " +
                        "WHERE a.deleted = 0 AND a.appoint_code = #{code}")
        AppointmentVO selectByCode(@Param("code") String code);

        /**
         * 统计不同捐赠者数量
         */
        @Select("SELECT COUNT(DISTINCT donor_id) FROM donation_appointment WHERE deleted = 0")
        Integer countDistinctDonors();

        /**
         * 统计月度预约数量
         */
        @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, COUNT(*) as count " +
                        "FROM donation_appointment WHERE deleted = 0 " +
                        "GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY month DESC LIMIT 12")
        List<java.util.Map<String, Object>> countMonthlyAppointments();

        /**
         * 查询志愿者的确认记录
         */
        @Select("SELECT a.*, " +
                        "u1.real_name as donorName, " +
                        "s.name as stationName " +
                        "FROM donation_appointment a " +
                        "LEFT JOIN sys_user u1 ON a.donor_id = u1.id " +
                        "LEFT JOIN collection_station s ON a.station_id = s.id " +
                        "WHERE a.deleted = 0 AND a.volunteer_id = #{volunteerId} " +
                        "ORDER BY a.update_time DESC")
        List<AppointmentVO> selectVolunteerConfirms(@Param("volunteerId") Long volunteerId);
}
