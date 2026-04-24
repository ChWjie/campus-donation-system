package com.campus.donation.common.enums;

/**
 * 预约状态枚举
 */
public enum AppointmentStatus {

    /**
     * 待接收
     */
    PENDING(0, "待接收"),

    /**
     * 已接收
     */
    RECEIVED(1, "已接收"),

    /**
     * 整理中
     */
    SORTING(2, "整理中"),

    /**
     * 已对接
     */
    DOCKED(3, "已对接"),

    /**
     * 已完成
     */
    COMPLETED(4, "已完成"),

    /**
     * 已取消
     */
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    AppointmentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AppointmentStatus fromCode(Integer code) {
        for (AppointmentStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的预约状态: " + code);
    }

    public static String getDescByCode(Integer code) {
        try {
            return fromCode(code).getDesc();
        } catch (IllegalArgumentException e) {
            return "未知状态";
        }
    }

}
