package com.zkx.httpbox.utils;

import com.alibaba.fastjson.JSON;
import com.zkx.httpbox.enums.ContentType;
import com.zkx.httpbox.enums.HttpMethod;
import com.zkx.httpbox.handler.RTHandler;
import com.zkx.httpbox.model.RT;
import com.zkx.notice.common.utils.JSONUtils;
import com.zkx.util.HttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
@Slf4j
public class HttpUtils {

    /**
     *  GET
     * @param url
     * @param header
     * @param requestBody
     * @param rthandler
     * @return
     */
    public static RT get(String url, Map<String,String> header, Map<String,Object> requestBody, RTHandler rthandler){
        HttpMethod method = HttpMethod.GET;
        String fullUrl = url + "?" + map2urlParameters(requestBody);
        Request.Builder builder = new Request.Builder()
                .get()//get请求
                .url(fullUrl);//请求地址
        if(!CollectionUtils.isEmpty(header)){
            builder.headers(Headers.of(header));
        }

        Request request = builder
                .build();//构建

        //通过HttpClient调用请求得到Call
        final Call call = HttpClientFactory.getInstance().newCall(request);
        return execute(fullUrl,method, header, requestBody, rthandler,  call);
    }


    /**
     * POST
     * @param url
     * @param contentType  application/json,  multipart/form-data  , application/x-www-form-urlencoded
     * @param header
     * @param requestBody
     * @param rthandler
     * @return
     */
    public static RT post(String url, String contentType, Map<String,String> header, Map<String,Object> requestBody, RTHandler rthandler){
        HttpMethod method = HttpMethod.POST;
        MediaType mediaType = MediaType.parse(contentType);
        ContentType contentTypeEnum = ContentType.of(contentType);

        RequestBody body = null;
        switch(contentTypeEnum ){
           case APPLICATION_X_WWW_FORM_URLENCODED:
               body =  fromUrlEncoded(requestBody);
               break;
           case MULTIPART_FORM_DATA:
               body = formData(requestBody);
               break;
           case APPLICATION_JSON:
           default:
               body = RequestBody.create(mediaType, JSON.toJSONString(requestBody));
       }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        builder.addHeader("Content-Type", contentType);
        if(!CollectionUtils.isEmpty(header)){
            for (String key : header.keySet()) {
                builder.addHeader(key,header.get(key));
            }
        }
        Request request = builder.build();//构建

        //通过HttpClient调用请求得到Call
        final Call call = HttpClientFactory.getInstance().newBuilder()
                .build().newCall(request);
        return execute(url,method, header, requestBody, rthandler,  call);
    }



    /**
     *  map 转 url参数 a=1&b=2
     * @param map
     * @return
     */
    public static String map2urlParameters(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                sb.append(key + "=");
                Object obj = map.get(key);
                if (obj == null ) {
                    sb.append("&");
                } else {
                    String value = obj == null ? "": String.valueOf(obj);
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(value + "&");
                }
            }
        }
        return sb.toString();
    }

    /**
     * multipart/form-data 表单
     * @param requestBody
     * @return
     */
    private static MultipartBody formData(Map<String, Object> requestBody) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (requestBody != null){
            for (String key : requestBody.keySet()) {
                builder.addFormDataPart(key,(String)requestBody.get(key));
            }
        }
        return builder.build();
    }

    /**
     *  application/x-www-form-urlencoded 数据是个普通表单；
     * @param
     * @return
     */
    private static FormBody fromUrlEncoded(Map<String, Object> map) {
        FormBody.Builder builder = new FormBody.Builder();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                if (StringUtils.isEmpty((String)map.get(key))) {
                    builder.add(key,"");
                } else {
                    String value = (String)map.get(key);
//                    try {
//                        value = URLEncoder.encode(value, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        log.error("error",e);
//                    }
                    builder.add(key,value);
                }
            }
        }
        return builder.build();
    }


    private static RT execute(String url, HttpMethod method, Map<String, String> header, Map<String, Object> requestBody, RTHandler rthandler,  Call call) {
        try {
            //执行同步请求，获取Response对象
            Response response = call.execute();
            if (response.isSuccessful()) {//如果请求成功
                log.info(">> http call method: {}  , url :{} ,  header:{} , reqBody:{} ; status ok "
                        , method, url, header, requestBody);
                return rthandler.parse(response);
            } else {
                log.error(">> http call method: {}  , url :{} ,  header:{} , reqBody:{} ;  response failed  code:{} "
                        , method, url, header, requestBody,response.code());
                throw new RuntimeException(" http call got exception ");
            }
        } catch ( IOException e) {
            log.error(">> http call method: {}  , url :{} ,  header:{} , reqBody:{} ;  got exception  "
                    , method, url, header, requestBody);
            throw new RuntimeException("http call got exception ",e);
        }
    }



}
