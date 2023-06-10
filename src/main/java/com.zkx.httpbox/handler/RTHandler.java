package com.zkx.httpbox.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zkx.httpbox.model.RT;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * response.body 字符串解析 [json格式]
 * 结果解析
 */
@Slf4j
public  class RTHandler {

    /**
     * 解析json格式
     * @param response
     * @return
     */
    public RT parse(Response response) {
        RT result = new RT();
        Map<String, Object> resultMap = new HashMap<>();
        String responseBodyStr = null;
        try {
            responseBodyStr = response.body().string();
            log.info(">> response body str :{} ", responseBodyStr);
        } catch (IOException e) {
            throw new RuntimeException("response.body().string() got exception ",e);
        }

        if(StringUtils.isNotEmpty(responseBodyStr)){
             try {
                JSONObject jsonObject = JSON.parseObject(responseBodyStr);
                resultMap.putAll(jsonObject);
            }catch (Exception  e){
                log.warn(" >> response is not json just string , set to responseBody.text ");
                resultMap.put("text",responseBodyStr);
            }
        }

        Map<String, String> headsToMap = headsToMap(response.headers());
        Map<String, String> cookies = headsToCookie(response.headers());

        result.setResponseHeaders(headsToMap);
        result.setResponseCookies(cookies);
        result.setResponseBody(resultMap);
        return result;
    }

    private Map<String, String> headsToCookie(Headers headers) {
        Map<String, String> cookiesMap = new LinkedHashMap<>();
        List<String> cookies = headers.values("Set-Cookie");

        for (String cookie : cookies) {
            String[] keyValues = cookie.split(";");
            if(keyValues.length > 0){
                for (String keyValue : keyValues) {
                    String[] split = keyValue.split("=");
                    if(split.length == 2 ){
                        cookiesMap.put(split[0],split[1]);
                    }
                }
            }
        }
       return cookiesMap;
    }


    /**
     *  请求头信息
     *
     * @param headers 请求头
     */
    private static Map<String, String> headsToMap(Headers headers) {
        Map<String, String> headersMap = new LinkedHashMap<>();
        if (headers == null) {
            return headersMap;
        }
        for (int i = 0; i < headers.size(); i++) {
            headersMap.put(headers.name(i) , headers.value(i));
        }
        return headersMap;
    }




}
