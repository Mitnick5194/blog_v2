package com.ajie.gateway.utils;

import com.ajie.gateway.config.Properties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径匹配工具
 */
public class PathUtil {

    private static final AntPathMatcher ANT = new AntPathMatcher();

    private PathUtil() {

    }

    /**
     * 断言验证登录，返回true需要登录验证，false放行
     *
     * @param path
     * @return
     */
    public static boolean assertAuth(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (matchIgnoreAuthPath(path)) {
            return false;//路径匹配上了忽略验证
        }
        return matchAuthPath(path);
    }

    private static boolean matchIgnoreAuthPath(String path) {
        List<String> patterns = Properties.ignoreAuthPath;
        return matchPath(patterns, path);

    }

    private static boolean matchAuthPath(String path) {
        List<String> patterns = Properties.authPath;
        return matchPath(patterns, path);
    }

    /**
     * uri路径是否符合patterns里面任意一条规则
     *
     * @param patterns
     * @param uri
     * @return
     */
    private static boolean matchPath(List<String> patterns, String uri) {
        if (CollectionUtils.isEmpty(patterns)) {
            return false;
        }
        for (String pattern : patterns) {
            if (ANT.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<String> ignoreUri = new ArrayList<>();
        ignoreUri.add("**/create*/**");
        ignoreUri.add("**/delete*/**");
        ignoreUri.add("*/update*/**");
        ignoreUri.add("/account*/**");

        boolean b = matchPath(ignoreUri, "micro-blog/1/2/3/v2/blog/create");
        System.out.println(b);
    }
}
