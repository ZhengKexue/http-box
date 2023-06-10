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

1.  pom 依赖引入
```java
    <dependency>
        <groupId>com.zkx</groupId>
        <artifactId>http-box</artifactId>
        <version>1.0.0</version>
    </dependency>

 ``` 

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
 
    @Test
    public void testExecute(){
        String json =  "    {\"requestList\":[\n" +
                "        {   \"name\":\"获取QQ头像\",\n" +
                "            \"description\":\"获取QQ头像\",\n" +
                "            \"url\":\"https://api.btstu.cn/qqxt/api.php?qq=920948763@qq.com\",\n" +
                "            \"contentType\":\"application/x-www-form-urlencoded\",\n" +
                "            \"method\":\"GET\",\n" +
                "            \"header\":{},\n" +
                "            \"body\":{\n" +
                "                \"qq\":\"920948763@qq.com\"\n" +
                "            },\n" +
                "            \"resultSuccessCheck\":\"#currentHttp.responseBody!=null &&  #currentHttp.responseBody.code==1\"\n" +
                "        }\n" +
                "    ]\n" +
                " }   ";


        HttpRequestChainConfig chainConfig = JSONUtils.parseObject(json,HttpRequestChainConfig.class);
        log.info(">> chainConfig:{}",JSONUtils.toJSONString(chainConfig));

        //构建OgnlContext上下文
        OgnlContext ognlContext = (OgnlContext) Ognl.createDefaultContext(new Object(), new DefaultClassResolver(),
                new DefaultTypeConverter());
        // Run the test

        httpBoxUnderTest.execute(chainConfig, ognlContext);
        Assertions.assertEquals(ognlContext != null ,true);
        log.info(">> ognlContext :{}", JSONUtils.toJSONString(ognlContext));
        //最后一个请求的返回结果 ognlContext.get("currentHttp")
        log.info(">> ognlContext :{}", JSONUtils.toJSONString(ognlContext.get("currentHttp")));
    }

 ``` 

####  3.  结果输出
结果都存储在了ognlContext中可以从中获取currentHttp ，最终响应结果是currentHttp.responseBody字段. imgurl 是对应的头像
```java
 
   # 获取最后一个请求的结果对象  ognlContext.get("currentHttp") 
   # currentHttp 对象结构
    {
    "requestHeaders": {"Header1":"1","Header2":"2"}, -- 请求头
    "requestBody": {},     -- 请求体
    "responseHeaders": {}, -- 响应头
    "responseCookies": {}, -- 响应Cookie
    "responseBody": {
        {"code":1,"imgurl":"https://q.qlogo.cn/headimg_dl?dst_uin=920948763@qq.com&spec=100","name":""}
     }     -- 响应体
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