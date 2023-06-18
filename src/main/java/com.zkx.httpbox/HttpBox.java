package com.zkx.httpbox;

import com.zkx.httpbox.utils.HttpUtils;
import com.zkx.httpbox.utils.OgnlUtils;
import com.zkx.httpbox.enums.HttpMethod;
import com.zkx.httpbox.handler.RTHandler;
import com.zkx.httpbox.model.HttpRequestChainConfig;
import com.zkx.httpbox.model.HttpRequestConfig;
import com.zkx.httpbox.model.RT;
import com.zkx.httpbox.model.ReRt;
import lombok.extern.slf4j.Slf4j;
import ognl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1.获取http请求链配置信息
 * 2.循环请求链，
 *   渲染当前请求参数，执行，获取结果，解析并判断是否继续，结果存入上下文
 *   继续执行下一条
 * 3.获得最终结果
 */
@Slf4j
public class HttpBox {

    /**
     *
     * 执行请求链
     * @param chainConfig
     * @param ognlContext
     *  过程存储
     *  1. httpConfigs 列表存储的是配置.
     *  1. httpList  列表存储的是每一次http请求的参数和返回内容.
     *  2. preHttp   存储的是后端对外的接口参数信息.
     *  2. lastHttp   存储的 httpList 的最后一条数据
     *
     * @return
     */
    public static void execute(HttpRequestChainConfig chainConfig, OgnlContext ognlContext){
        List<HttpRequestConfig> requestConfigs = chainConfig.getRequestList();
        if(requestConfigs == null){
            log.info(">> requestConfig list  is null ");
            return;
        }

        for (HttpRequestConfig requestConfig : requestConfigs) {
            Boolean successFlag = execute(requestConfig, ognlContext);
            if(!Boolean.TRUE.equals(successFlag)){
                log.info(">> current request response successFlag is false , quit it , config is :{} ",requestConfig);
                throw new RuntimeException("current request response successFlag is false " );
            }
        }
    }

    /**
     * 执行单个请求
     * @param
     * @param ognlContext
     * @return
     */
    public static Boolean execute(HttpRequestConfig requestConfig, OgnlContext ognlContext){
        //解析
        String url = requestConfig.getUrl();
        String method = requestConfig.getMethod();
        String contentType = requestConfig.getContentType();
        Map<String, String> header = OgnlUtils.render(requestConfig.getHeader(),ognlContext);
        Map<String, Object> body = OgnlUtils.renderObjectMap(requestConfig.getBody(),ognlContext);;

        //执行http请求
        RT rt = null ;
        if(HttpMethod.GET.matches(method)){
             rt = HttpUtils.get(url, header, body, new RTHandler());
        }else if (HttpMethod.POST.matches(method)) {
             rt = HttpUtils.post(url,contentType,header,body,new RTHandler());
        }else {
            throw new UnsupportedOperationException("method 不支持");
        }

        //结果构建
        ReRt reRt = new ReRt();
        reRt.setRequestHeaders(header);
        reRt.setRequestBody(body);
        reRt.setResponseHeaders(rt.getResponseHeaders());
        reRt.setResponseBody(rt.getResponseBody());
        reRt.setResponseCookies(rt.getResponseCookies());

        List<ReRt> httpList = (List<ReRt>) OgnlUtils.getValue("#httpList",ognlContext);
        if(httpList == null ){
            httpList = new ArrayList<>();
        }
        httpList.add(reRt);
        ognlContext.put("httpList",httpList);
        ognlContext.put("currentHttp",reRt);
        ognlContext.put("lastHttp",reRt);

        Boolean successFlag = (Boolean)OgnlUtils.getValue(requestConfig.getResultSuccessCheck(), ognlContext);
        return successFlag;
    }

}
