package com.ajie.commons.encrypt;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import sun.applet.Main;

public final class EncryptUtil {

    private EncryptUtil() {

    }

    /**
     * sha256签名
     *
     * @param content
     * @return
     */
    public static String sha256Encrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return DigestUtils.sha256Hex(content);
    }

    /**
     * sha1签名
     *
     * @param content
     * @return
     */
    public static String sha1Encrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return DigestUtils.sha1Hex(content);
    }

    public static String md5Encrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        return DigestUtils.md5Hex(content);
    }

    public static void main(String[] args) {
        String content = "hello world";
        String s = md5Encrypt(content);
        System.out.println(s);
    }


}
