package com.jun.po.model;

public class HttpRequestParams {
    private String url;//请求URL
    private long time;//请求时时间戳

    public HttpRequestParams(String url, long time) {
        this.url = url;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public long getTime() {
        return time;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTime(long time) {
        this.time = time;
    }
}