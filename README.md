# http-box

### 介绍
http-box 是一个基于json配置的http 请求链式处理器，通过json定义http请求的过程，通过ognl表达式配置动态内容，方便参数任意组合串联多个请求之间传递

#### 解决的问题
对接外部接口是一个重复且麻烦的事情，每次对接外部接口不同需要写不同的对接代码，复用不高，http-box 基于配置来对接外部接口解决hardcode编码问题

#### 目标
1. 配置化接入新三方平台账号，无开发或者少量开发（主要是插件开发）
2. 支持HTTP协议方式接入
3. 支持灵活的表达式配置HTTP请求的url,method,header,body等信息
4. 支持插件扩展功能（有部分平台请求参数需要计算签名,Md5,Hash值等）

### 软件架构
软件架构说明
基于 okhttp3 作为http客户端请求工具 + ognl 作为动态脚本


### 安装教程

1. git clone https://gitee.com/kexuezheng/http-box.git 到本地文件
2. idea 打开工程 http-box
 

### 使用说明

例子1: 获取QQ昵称和头像，接口文档 https://api.btstu.cn/doc/qqxt.php
#### 1.  配置http请求链json
    
```java
  {"requestList":[
        {   "name":"获取QQ头像",
            "description":"获取QQ头像",
            "url":"https://api.btstu.cn/qqxt/api.php?qq=920948763@qq.com",
            "contentType":"application/x-www-form-urlencoded",
            "method":"GET",
            "header":{},
            "body":{
                "qq":"920948763@qq.com"
            },
            "resultSuccessCheck":"#currentHttp.responseBody!=null &&  #currentHttp.responseBody.code==1"
        }
    ]
 }  


 ``` 

配置属性说明：
- requestList[]  请求配置列表，由一个或者多个请求组成
  - name            请求名称
  - description    描述
  - url                请求路径url
  - contentType  请求类型 （json/form-data/urlencode） 
  - method         请求方法 GET/POST/PUT 等
  - header          请求头（可嵌入表达式）
  - body             请求体 （可嵌入表达式）
  - resultSuccessCheck   响应结果成功检查表达式 （检查失败情况会抛出exception）

 OGNL上下文内置对象说明： 为了方便在请求链中传递参数，在OGNL上下文（Map）中内置了几个默认的对象如下：  
- preHttp       来自浏览器请求的HTTP 请求对象
- currentHttp  链式请求执行器中当前正在处理的HTTP 请求对象 
- httpList       链式请求执行器执行后结果放入HTTP 请求对象列表


####  2.  通过HttpBox发起请求
    
```java
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

 ``` 


### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目 