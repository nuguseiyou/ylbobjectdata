package com.nuguseiyou.micrweb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 2021/9/17
 */
@Component
@ConfigurationProperties(prefix = "jdwx.sms")
public class JdwxSmsConfig {
    //访问的接口
    private String url;
    //短信内容
    private String content;
    //网站申请appkey
    private String appkey;

    @Override
    public String toString() {
        return "JdwxSmsConfig{" +
                "url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", appkey='" + appkey + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }
}
