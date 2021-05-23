package com.ajie.utils;

import com.ajie.dto.JwtAccount;
import com.ajie.exception.VerifyException;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

public class JwtUtil {
    private JwtUtil() {

    }

    /**
     * 检验合法性
     *
     * @param token  param
     * @param secret 秘钥
     * @throws
     */
    public static JwtAccount verifyToken(String token, String secret) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            verifier.verify(token);
            //解析jwt
            DecodedJWT decode = JWT.decode(token);
            String payload = decode.getPayload();
            //解析base64
            String json = StringUtils.newStringUtf8(Base64.decodeBase64(payload));
            JwtAccount jwtAccount = JSON.parseObject(json, JwtAccount.class);
            jwtAccount.setId(Long.valueOf(decode.getAudience().get(0)));
            return jwtAccount;
        } catch (Exception e) {
            throw new VerifyException(e);
        }
    }

}
