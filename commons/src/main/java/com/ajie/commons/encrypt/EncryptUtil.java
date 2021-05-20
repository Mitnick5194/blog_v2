package com.ajie.commons.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import sun.applet.Main;

import java.security.*;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 生成rsa秘钥对
     *
     * @return
     */
    public static Map<String, String> genRsaKeyPair() throws NoSuchAlgorithmException {
        Map<String, String> map = new HashMap<>();
        KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = rsa.genKeyPair();
        PublicKey aPublic = keyPair.getPublic();
        PrivateKey aPrivate = keyPair.getPrivate();
        map.put("publicKey", Base64.encodeBase64String(aPublic.getEncoded()));
        map.put("privateKey", Base64.encodeBase64String(aPrivate.getEncoded()));
        return map;
    }

    public static void main(String[] args) throws Exception {
        String content = "hello world";
        String s = md5Encrypt(content);
        System.out.println(s);
        Map<String, String> map = genRsaKeyPair();
        System.out.println(map.get("publicKey"));
        System.out.println(map.get("privateKey"));

    }


}
