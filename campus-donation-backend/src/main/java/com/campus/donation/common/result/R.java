package com.campus.donation.common.result;

import java.io.Serializable;

/**
 * 统一响应结构
 *
 * @param <T> 数据类型
 */
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public R() {
    }

    public R(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    /**
     * 成功响应
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    /**
     * 成功响应(无数据)
     */
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功", null);
    }

    /**
     * 成功响应(自定义消息)
     */
    public static <T> R<T> ok(String message, T data) {
        return new R<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> R<T> fail(String message) {
        return new R<>(500, message, null);
    }

    /**
     * 失败响应(自定义错误码)
     */
    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message, null);
    }

    /**
     * 未授权响应
     */
    public static <T> R<T> unauthorized() {
        return new R<>(401, "未授权,请登录", null);
    }

    /**
     * 未授权响应(自定义消息)
     */
    public static <T> R<T> unauthorized(String message) {
        return new R<>(401, message, null);
    }

    /**
     * 禁止访问响应
     */
    public static <T> R<T> forbidden() {
        return new R<>(403, "权限不足,禁止访问", null);
    }

    /**
     * 资源未找到响应
     */
    public static <T> R<T> notFound(String message) {
        return new R<>(404, message, null);
    }

}
