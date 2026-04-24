package com.campus.donation.module.donation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.donation.module.donation.dto.AppointmentDTO;
import com.campus.donation.module.donation.entity.DonationAppointment;
import com.campus.donation.module.donation.vo.AppointmentVO;

import java.util.List;

/**
 * 捐赠预约服务接口
 */
public interface DonationAppointmentService extends IService<DonationAppointment> {

    /**
     * 创建捐赠预约
     * 
     * @param dto     预约信息
     * @param donorId 捐赠者ID
     * @return 预约VO（含二维码）
     */
    AppointmentVO createAppointment(AppointmentDTO dto, Long donorId);

    /**
     * 查询我的预约列表
     */
    List<AppointmentVO> getMyAppointments(Long donorId);

    /**
     * 查询预约详情
     */
    AppointmentVO getAppointmentDetail(Long id);

    /**
     * 志愿者扫码确认接收
     * 
     * @param appointCode 预约码
     * @param volunteerId 志愿者ID
     * @param itemType 旧物类型
     * @param itemCondition 旧物完好度
     */
    void volunteerConfirm(String appointCode, Long volunteerId, String itemType, String itemCondition);

    /**
     * 更新预约状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 捐赠者取消预约
     */
    void cancelAppointment(Long appointmentId, Long donorId);

    /**
     * 查询志愿者的确认记录
     */
    List<AppointmentVO> getVolunteerConfirms(Long volunteerId);
}
