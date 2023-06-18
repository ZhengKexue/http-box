package com.zkx.httpbox.enums;

public enum ContentType {
    APPLICATION_JSON("application/json"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded");

    private String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }


    public static ContentType of(String contentType){
        if(contentType ==null){
            return null;
        }
        for (ContentType value : values()) {
            if(value.getContentType().equalsIgnoreCase(contentType)){
                return value;
            }
        }
        return null;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
