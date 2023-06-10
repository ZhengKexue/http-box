package com.zkx.httpbox.model;


import java.io.Serializable;
import java.util.Map;

/**
 * http response
 */
public class RT implements Serializable {


    private Map<String,String>  responseHeaders;

    private Map<String,String>  responseCookies;

    private Map<String,Object>  responseBody;



    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Map<String, Object> getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Map<String, Object> responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, String> getResponseCookies() {
        return responseCookies;
    }

    public void setResponseCookies(Map<String, String> responseCookies) {
        this.responseCookies = responseCookies;
    }
}
