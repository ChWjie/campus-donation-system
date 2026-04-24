package com.campus.donation.module.charity.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公益对接实体类
 */
@TableName("charity_docking")
public class CharityDocking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dockingNo;

    private Long operatorId;

    private String charityName;

    private String charityContact;

    private String purpose;

    private String itemIds;

    private Integer itemCount;

    private Integer status;

    private String feedbackText;

    @TableField("feedback_images")
    private String feedbackImages;

    private LocalDateTime dockingTime;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDockingNo() {
        return dockingNo;
    }

    public void setDockingNo(String dockingNo) {
        this.dockingNo = dockingNo;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public String getCharityContact() {
        return charityContact;
    }

    public void setCharityContact(String charityContact) {
        this.charityContact = charityContact;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getItemIds() {
        return itemIds;
    }

    public void setItemIds(String itemIds) {
        this.itemIds = itemIds;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public String getFeedbackImages() {
        return feedbackImages;
    }

    public void setFeedbackImages(String feedbackImages) {
        this.feedbackImages = feedbackImages;
    }

    public LocalDateTime getDockingTime() {
        return dockingTime;
    }

    public void setDockingTime(LocalDateTime dockingTime) {
        this.dockingTime = dockingTime;
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
