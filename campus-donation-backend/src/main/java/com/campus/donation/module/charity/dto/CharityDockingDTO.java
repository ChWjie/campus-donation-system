package com.campus.donation.module.charity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 公益对接DTO
 */
public class CharityDockingDTO {

    @NotBlank(message = "受赠组织名称不能为空")
    private String charityName;

    @NotBlank(message = "受赠方联系方式不能为空")
    private String charityContact;

    private String purpose;

    @NotBlank(message = "旧物ID不能为空")
    private String itemIds; // 逗号分隔的旧物ID

    @NotNull(message = "对接数量不能为空")
    private Integer itemCount;

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
}
