package com.iscas.lndicatormonitor.common;

public class Result<T> {
    private String code;
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public static Result success() {
        Result result = new Result<>();
        result.setCode("20000");
        result.setMsg("成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(data);
        result.setCode("20000");
        result.setMsg("成功");
        return result;
    }

    public static <T> Result<T> success(T data,String message) {
        Result<T> result = new Result<>(data);
        result.setCode("20000");
        result.setMsg(message);
        return result;
    }

    public static Result error(String code, String message) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }
}
