package com.qiang.taskmanager.common;

import lombok.Data;

/**
 * 通用响应VO
 */
@Data
public class Result<T> {
    private int code; // 状态码
    private String message; // 消息
    private T data; // 数据

    public static <T> Result<T> success(T data)  {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success()  {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(null);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}