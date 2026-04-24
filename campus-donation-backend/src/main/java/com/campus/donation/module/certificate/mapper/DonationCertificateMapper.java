package com.campus.donation.module.certificate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.donation.module.certificate.entity.DonationCertificate;
import com.campus.donation.module.certificate.vo.DonationCertificateVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 电子捐赠证明 Mapper
 */
@Mapper
public interface DonationCertificateMapper extends BaseMapper<DonationCertificate> {

    @Select("SELECT c.id, c.donor_id, c.appointment_id, c.cert_no, c.cert_url, c.issue_time, " +
            "u.real_name AS donorName, a.appoint_code AS appointCode, a.item_type AS itemType, " +
            "a.item_quantity AS itemQuantity, a.item_desc AS itemDesc, a.appoint_time AS appointTime, " +
            "s.name AS stationName " +
            "FROM donation_certificate c " +
            "LEFT JOIN donation_appointment a ON c.appointment_id = a.id " +
            "LEFT JOIN sys_user u ON c.donor_id = u.id " +
            "LEFT JOIN collection_station s ON a.station_id = s.id " +
            "WHERE c.deleted = 0 AND c.donor_id = #{donorId} " +
            "ORDER BY c.issue_time DESC")
    List<DonationCertificateVO> selectMyCertificates(@Param("donorId") Long donorId);

    @Select("SELECT c.id, c.donor_id, c.appointment_id, c.cert_no, c.cert_url, c.issue_time, " +
            "u.real_name AS donorName, a.appoint_code AS appointCode, a.item_type AS itemType, " +
            "a.item_quantity AS itemQuantity, a.item_desc AS itemDesc, a.appoint_time AS appointTime, " +
            "s.name AS stationName " +
            "FROM donation_certificate c " +
            "LEFT JOIN donation_appointment a ON c.appointment_id = a.id " +
            "LEFT JOIN sys_user u ON c.donor_id = u.id " +
            "LEFT JOIN collection_station s ON a.station_id = s.id " +
            "WHERE c.deleted = 0 AND c.id = #{id}")
    DonationCertificateVO selectDetailById(@Param("id") Long id);

    @Select("SELECT c.id, c.donor_id, c.appointment_id, c.cert_no, c.cert_url, c.issue_time, " +
            "u.real_name AS donorName, a.appoint_code AS appointCode, a.item_type AS itemType, " +
            "a.item_quantity AS itemQuantity, a.item_desc AS itemDesc, a.appoint_time AS appointTime, " +
            "s.name AS stationName " +
            "FROM donation_certificate c " +
            "LEFT JOIN donation_appointment a ON c.appointment_id = a.id " +
            "LEFT JOIN sys_user u ON c.donor_id = u.id " +
            "LEFT JOIN collection_station s ON a.station_id = s.id " +
            "WHERE c.deleted = 0 AND c.appointment_id = #{appointmentId} LIMIT 1")
    DonationCertificateVO selectByAppointmentId(@Param("appointmentId") Long appointmentId);
}
