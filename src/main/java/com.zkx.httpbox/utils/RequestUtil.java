package com.zkx.httpbox.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
@Slf4j
public class RequestUtil {

    /**
     * 解析http urlencode + formdata
     * @param request
     * @return
     */
    public static Map<String, String> getParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                if (paramValues[0].length() != 0) {
                    params.put(paramName, paramValues[0]);
                }
            }
        }
        return params;
    }

    /**
     * 解析http application/json
     * @param request
     * @return
     */
    public static Map<String, Object> getJSON(HttpServletRequest request)  {
        BufferedReader streamReader = null;
        try {
            streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            log.warn(">> try to read request inputstream   got exception :{}",e);
        }
        StringBuilder sb = new StringBuilder();
        String inputStr;
        while (true) {
            try {
                if (!((inputStr = streamReader.readLine()) != null)) break;
                sb.append(inputStr);
            } catch (IOException e) {
                log.warn(">> try to read inputstream   got exception :{}",e);
            }
        }
        if(StringUtils.isNotEmpty(sb)){
            try {
                JSONObject jsonObject = JSONObject.parseObject(sb.toString());
                return jsonObject;
            }catch (Exception e){
                log.warn(">> try to parse request inputstream to json  got exception :{}",e);
            }
        }
        return Maps.newHashMap();
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name	= enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }
}
