package com.script.domain.vo;

public class R<T> {

    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok() {
        return R.ok(null);
    }

    public static <T> R<T> ok(T data) {
        return R.of(200, "success", data);
    }

    public static <T> R<T> fail(int code, String message) {
        return R.of(code, message, null);
    }

    public static <T> R<T> of(int code, String message, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        r.setData(data);
        return r;
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
