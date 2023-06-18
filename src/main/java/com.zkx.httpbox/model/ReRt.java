package com.zkx.httpbox.model;

import lombok.Data;

import java.util.Map;

/**
 * http 请求 + 回答
 */
@Data
public class ReRt extends RT{


    private Map<String,String> requestHeaders;

    private Map<String,Object>  requestBody;


}
