package com.zkx.httpbox.utils;

import com.zkx.httpbox.model.ReRt;
import lombok.extern.slf4j.Slf4j;
import ognl.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class OgnlUtils {

    public static final String PRE_HTTP = "#preHttp";
    public static final String LAST_HTTP = "#lastHttp";
    public static final String CURRENT_HTTP = "#currentHttp";
    public static final String HTTP_LIST = "#httpList";


    /**
     * 根据外部的httpRequest 请求构建 OgnlContext
     * @param httpRequest
     * @return
     */
    public static OgnlContext builderContext(HttpServletRequest httpRequest){
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(new Object());
        ReRt reRt = new ReRt();
        Map<String, String> headers = RequestUtil.getHeaders(httpRequest);
        reRt.setRequestHeaders(headers);
        //Optional<String> isrequestJson = headers.values().stream().filter(e -> e.indexOf("application/json") >= 0).findFirst();
        //if(isrequestJson.isPresent()){
        //}

        //解析params
        reRt.setRequestBody((Map)RequestUtil.getParams(httpRequest));

        //尝试解析json
        Map<String, Object> requestBody = reRt.getRequestBody();
        if(requestBody == null ){
            requestBody = new HashMap<>();
        }
        requestBody.putAll(RequestUtil.getJSON(httpRequest));
        reRt.setRequestBody(requestBody);
        context.put("preHttp",reRt);
        return context;
    }


    /**
     * 渲染
     * @param source
     * @param ognlContext
     * @return
     *
     */
    public static Map<String, Object> renderObjectMap(Map<String, Object> source, OgnlContext ognlContext){
        Map<String,Object> target = new LinkedHashMap<>();
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if(value == null){
                target.put(key,value);
            }else  if(value instanceof String  ){
                String valueStr = (String) value;
                if(isOgnlExpression(valueStr)){
                    Object expression = null ;
                    try {
                        //Ognl.get
                        expression = Ognl.parseExpression(valueStr);
                        // Ognl.getValue(Ognl.parseExpression((String)value),ognlContext);
                        Object result = Ognl.getValue(expression, ognlContext, ognlContext.getRoot());
                        target.put(key,result);
                    } catch (Exception e) {
                        log.error("while parse ognl Expression got exception", e);
                        target.put(key,value);
                    }
                }else {
                    target.put(key,value);
                }
            }
        }
        return target;
    }

    private static boolean isOgnlExpression(String valueStr) {
        return valueStr.startsWith("#") || valueStr.startsWith("#@") || valueStr.indexOf("#") >=0 || valueStr.startsWith("@");
    }

    public static  Object getValue(String el,  OgnlContext ognlContext){
        try {
            if(!isOgnlExpression(el)){
                return el;
            }
            //Ognl.get
            // Ognl.getValue(Ognl.parseExpression((String)value),ognlContext);
            Object result = Ognl.getValue(el, ognlContext, ognlContext);
            return result;
        } catch (Exception e) {
            log.error("while  parseExpression  got exception [{}] ",el, e);
            throw new RuntimeException(" while  parseExpression  got exception ," + el );
        }
    }

    public static  Map<String, String> render(Map<String,String> newLinkedHashMap,  OgnlContext ognlContext) {
        Map<String, Object> source = new LinkedHashMap<>();
        for (String s : newLinkedHashMap.keySet()) {
            source.put(s,newLinkedHashMap.get(s));
        }
        Map<String, Object> render = renderObjectMap(source, ognlContext);

        Map<String, String> target = new LinkedHashMap<>();
        for (String s : render.keySet()) {
            target.put(s,(String)render.get(s));
        }

        return target;
    }

    public static void main(String[] args) throws OgnlException {

         OgnlContext context = (OgnlContext) Ognl.createDefaultContext(new Object(), new DefaultClassResolver(),
                new DefaultTypeConverter());

        context.setRoot(new Object());
        context.put("value","abcd");
//        Ognl.isConstant("value1");
//        Ognl.isConstant("#value1");
//        Object expression = Ognl.parseExpression("value1");
        //Ognl.getValue(opLog.opNoOgnl(), context, AppEnv.getDeployEnv());
         Object result = getValue("value",context);
         System.out.println(result);
         result = getValue("#value+'asdad'",context);
         System.out.println(result);
        result = getValue("@org.apache.commons.codec.digest.DigestUtils@sha256Hex('Aa111111')",context);
        System.out.println(result);

        ;//b7e3f6b21b1e5f755036eeb2f110f88841c9e684c0ee125038dfe35fd6646887
//        System.out.println(DigestUtils.sha256Hex("Aa111111"));
//        System.out.println(org.apache.commons.codec.digest.DigestUtils.sha256Hex("Aa111111"));

     }
}
