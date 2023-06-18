package com.zkx.httpbox;

import com.alibaba.fastjson.JSON;
import com.zkx.httpbox.model.HttpRequestChainConfig;
import com.zkx.httpbox.model.HttpRequestConfig;
import com.zkx.httpbox.model.ReRt;
import com.zkx.httpbox.utils.OgnlUtils;
import lombok.extern.slf4j.Slf4j;
import ognl.OgnlContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class HttpBoxTest {

    /**
     *
     * 入门案例
     */
    @Test
    public  void testExecute1() {

        // Setup, 请求链对象定义: 一个请求获取qq头像
        final HttpRequestChainConfig chainConfig  = JSON.parseObject("{\n" +
                "      \"requestList\":[\n" +
                "         {   \"name\":\"获取QQ头像\",\n" +
                "         \"description\":\"获取QQ头像\",\n" +
                "         \"url\":\"https://api.btstu.cn/qqxt/api.php?qq=920948763@qq.com\",\n" +
                "         \"contentType\":\"application/x-www-form-urlencoded\",\n" +
                "         \"method\":\"GET\",\n" +
                "         \"header\":{},\n" +
                "         \"body\":{\n" +
                "         \"qq\":\"920948763@qq.com\"\n" +
                "         },\n" +
                "       \"resultSuccessCheck\":\"#currentHttp.responseBody!=null &&  #currentHttp.responseBody.code==1\"}\n" +
                "      ]\n" +
                "     }",HttpRequestChainConfig.class);

        //上下文初始化
        final OgnlContext ognlContext = new OgnlContext(new HashMap<>());

        // 执行请求链
        HttpBox.execute(chainConfig, ognlContext);

        //获得结果
        //{"requestBody":{"qq":"920948763@qq.com"},"requestHeaders":{},"responseBody":{"name":"","imgurl":"https://q.qlogo.cn/headimg_dl?dst_uin=920948763@qq.com?qq=920948763@qq.com&spec=100","code":1},"responseCookies":{"PHPSESSID":"7eg0ne101e4c5q6a02h02v5npu"," path":"/"},"responseHeaders":{"Server":"nginx","Date":"Sun, 18 Jun 2023 16:09:47 GMT","Content-Type":"text/html; charset=UTF-8","Transfer-Encoding":"chunked","Connection":"keep-alive","Vary":"Accept-Encoding","Set-Cookie":"PHPSESSID=7eg0ne101e4c5q6a02h02v5npu; path=/","Expires":"Thu, 19 Nov 1981 08:52:00 GMT","Cache-Control":"no-store, no-cache, must-revalidate","Pragma":"no-cache","Access-Control-Allow-Origin":"*","Strict-Transport-Security":"max-age=31536000"}}
        ReRt reRt = (ReRt)OgnlUtils.getValue(OgnlUtils.CURRENT_HTTP, ognlContext);
        System.out.printf(JSON.toJSONString(reRt));
    }

}
