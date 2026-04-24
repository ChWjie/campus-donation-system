package com.campus.donation.module.donation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.campus.donation.common.enums.AppointmentStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 捐赠预约实体类
 */
@TableName("donation_appointment")
public class DonationAppointment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 预约ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 捐赠者ID
     */
    private Long donorId;

    /**
     * 预约码
     */
    private String appointCode;

    /**
     * 旧物类型
     */
    private String itemType;

    /**
     * 数量
     */
    private Integer itemQuantity;

    /**
     * 完好度
     */
    private String itemCondition;

    /**
     * 旧物描述
     */
    private String itemDesc;

    /**
     * 回收点ID
     */
    private Long stationId;

    /**
     * 预约回收时间
     */
    private LocalDateTime appointTime;

    /**
     * 状态: 0-待接收 1-已接收 2-整理中 3-已对接 4-已完成 5-已取消
     */
    private Integer status;

    /**
     * 负责志愿者ID
     */
    private Long volunteerId;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getAppointCode() {
        return appointCode;
    }

    public void setAppointCode(String appointCode) {
        this.appointCode = appointCode;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }

    public LocalDateTime getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(LocalDateTime appointTime) {
        this.appointTime = appointTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
