package com.ajie.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 配置属性，可以使用propertyiesUtil获取
 */
@Component
@ConfigurationProperties(prefix = "properties")
@RefreshScope
public class Properties {
    public static String tokenSecret;
    /**
     * 忽略拦截路径，与下面的拦截路径互斥，先匹配忽略拦截路径，如存在，则跳过拦截
     * 如不存在，则去拦截路径匹配
     * 此属性主要用于与路径规则与authPath匹配，但又不需要验证的路径
     */
    public static List<String> ignoreAuthPath;

    /**
     * 登录拦截路径，如存在，需要验证是否登录，优先级小于ignoreAuthPath
     */
    public static List<String> authPath;

    /**
     * 上传文件保存的文件夹
     */
    public static String uploadFileDir;

    /**
     * 访问上面的文件夹的链接
     */
    public static String uploadFileUrl;

    /**
     * 前端富文本获取图片的中转处理页面
     */
    public static String frontGetImageHtml;

    public void setTokenSecret(String secret) {
        tokenSecret = secret;
    }

    public void setIgnoreAuthPath(List<String> paths) {
        ignoreAuthPath = paths;
    }

    public void setAuthPath(List<String> paths) {
        authPath = paths;
    }

    public void setUploadFileDir(String uploadFileDir) {
        Properties.uploadFileDir = uploadFileDir;
    }

    public void setUploadFileUrl(String uploadFileUrl) {
        Properties.uploadFileUrl = uploadFileUrl;
    }

    public void setFrontGetImageHtml(String frontGetImageHtml) {
        Properties.frontGetImageHtml = frontGetImageHtml;
    }
}
