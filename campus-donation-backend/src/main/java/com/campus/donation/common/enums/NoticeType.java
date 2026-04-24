package com.campus.donation.common.enums;

/**
 * 通知类型枚举
 */
public enum NoticeType {

    /**
     * 预约提醒
     */
    APPOINT_REMINDER(1, "预约提醒"),

    /**
     * 接收确认
     */
    RECEIVE_CONFIRM(2, "接收确认"),

    /**
     * 对接完成
     */
    DOCKING_COMPLETE(3, "对接完成"),

    /**
     * 公益反馈
     */
    CHARITY_FEEDBACK(4, "公益反馈");

    private final Integer code;
    private final String desc;

    NoticeType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
