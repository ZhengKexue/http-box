package com.zkx.httpbox.model;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 {
 "name":"getAccessToken",
 "description":"获取accessToken",
 "url":"http://127.0.0.1:8080/login",
 "method":"POST",
 "contentType":"application/x-www-form-urlencoded",
 "header":{
     "token":"token",
     "token2":"#param.token"
 },
 "body":{
     "name":"name",
     "password":"#param.password"
 },
 "resultSuccessCheck":"#result.userId!=null"
 }
 */
@Data
@ToString
public class HttpRequestConfig {

    //名称
    private String	name;

    /**
     * 描述
     */
    private String	description;

    /**
     * url地址
     */
    private String	url;

    /**
     * 请求方式 GET,POST
     */
    private String	method;

    /**
     *  类型,支持如下三种
     *  application/x-www-form-urlencoded
     *  application/json
     *  multipart/form-data
     */
    private String	contentType;

    /**
     * 请求头
     */
    private Map<String,String> header;

    /**
     * 请求体
     */
    private Map<String,Object>	body;


    /**
     * 内部配置信息【可选，看情况使用】
     */
    private Map<String,Object>	config;

    /**
     *  验证结果是否成功的表达式
     */
    private String	resultSuccessCheck;

}
