package com.campus.donation.module.donation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 捐赠预约DTO
 */
public class AppointmentDTO {

    /**
     * 旧物类型
     */
    @NotBlank(message = "旧物类型不能为空")
    private String itemType;

    /**
     * 数量
     */
    @NotNull(message = "旧物数量不能为空")
    @Min(value = 1, message = "旧物数量必须大于0")
    private Integer itemQuantity;

    /**
     * 完好度
     */
    @NotBlank(message = "完好度不能为空")
    private String itemCondition;

    /**
     * 旧物描述
     */
    @NotBlank(message = "旧物描述不能为空")
    @Size(max = 500, message = "旧物描述长度不能超过500")
    private String itemDesc;

    /**
     * 回收点ID
     */
    @NotNull(message = "回收点不能为空")
    private Long stationId;

    /**
     * 预约回收时间
     */
    @NotNull(message = "预约时间不能为空")
    @Future(message = "预约时间必须是未来时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointTime;

    // Getters and Setters
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
}
