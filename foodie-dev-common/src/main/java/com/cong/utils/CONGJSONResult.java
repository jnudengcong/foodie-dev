package com.cong.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Description 自定义响应数据结构
 *              本来可提供给 H5/安卓/ios/公众号/小程序 使用
 *
 *              200：成功
 *              500：错误，错误信息在字段msg中
 *              501：bean验证错误，不管多少个错误都以map形式返回
 *              502：拦截器拦截到用户token出错
 *              555：异常抛出信息
 *              556：用户qq校验异常
 */
public class CONGJSONResult {

    // 定义jackson对象
    public static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    @JsonIgnore
    private String ok; // 不使用

    public static CONGJSONResult build(Integer status, String msg, Object data) {
        return new CONGJSONResult(status, msg, data);
    }

    public static CONGJSONResult build(Integer status, String msg, Object data, String ok) {
        return new CONGJSONResult(status, msg, data, ok);
    }

    public static CONGJSONResult ok(Object data) {
        return new CONGJSONResult(data);
    }

    public static CONGJSONResult ok() {
        return new CONGJSONResult(null);
    }

    public static CONGJSONResult errorMsg(String msg) {
        return new CONGJSONResult(500, msg, null);
    }

    public static CONGJSONResult errorMap(Object data) {
        return new CONGJSONResult(501, "error", data);
    }

    public static CONGJSONResult errorTokenMsg(String msg) {
        return new CONGJSONResult(502, msg, null);
    }

    public static CONGJSONResult errorException(String msg) {
        return new CONGJSONResult(555, msg, null);
    }

    public static CONGJSONResult errorUserQQ(String msg) {
        return new CONGJSONResult(556, msg, null);
    }

    public CONGJSONResult() {}

    public CONGJSONResult(Integer status, String msg, Object data, String ok) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        this.ok = ok;
    }

    public CONGJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public CONGJSONResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    public Boolean isOK() {
        return this.status == 200;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOK() {
        return ok;
    }

    public void setOK(String ok) {
        this.ok = ok;
    }
}
