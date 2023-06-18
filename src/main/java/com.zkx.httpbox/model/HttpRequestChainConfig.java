package com.zkx.httpbox.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class HttpRequestChainConfig implements Serializable {

    /**
     * 页面配置,[比如跳转路径，url地址等，不能放敏感信息]
     */
    private Map<String,Object> pageConfig ;

    private List<HttpRequestConfig> requestList ;

}
