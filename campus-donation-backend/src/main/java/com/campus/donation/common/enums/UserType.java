package com.campus.donation.common.enums;

/**
 * 用户类型枚举
 */
public enum UserType {

    /**
     * 捐赠者
     */
    DONOR(1, "捐赠者"),

    /**
     * 志愿者
     */
    VOLUNTEER(2, "志愿者"),

    /**
     * 公益对接员
     */
    CHARITY_OPERATOR(3, "公益对接员"),

    /**
     * 管理员
     */
    ADMIN(4, "管理员");

    private final Integer code;
    private final String desc;

    UserType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserType fromCode(Integer code) {
        for (UserType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的用户类型: " + code);
    }

}
