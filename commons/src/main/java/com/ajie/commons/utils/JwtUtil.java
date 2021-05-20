package com.ajie.commons.utils;

import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.exception.MicroCommonException;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.Date;

public class JwtUtil {
    private JwtUtil() {

    }

    /**
     * 创建token，使用默认过期时间2小时
     *
     * @param secret  秘钥
     * @param account 用戶
     * @return
     */
    public static String createToken(String secret, JwtAccount account) {
        Date date = new Date();
        Date expire = DateUtil.plusMills(date, DateUtil.MILLI_OF_HOUR * 2);//两小时过期
        return createToken(secret, expire, account);
    }

    /**
     * 创建token，指定expireTime后过期
     *
     * @param secret     秘钥
     * @param expireTime 过期时间，单位s
     * @param account    用戶
     * @return
     */
    public static String createToken(String secret, long expireTime, JwtAccount account) {
        Date date = new Date();
        Date expire = DateUtil.plusMills(date, expireTime);//计算过期时间
        return createToken(secret, expire, account);
    }

    /**
     * 创建token，指定过期时间
     *
     * @param secret     秘钥
     * @param expireTime 过期时间
     * @param account    用戶
     * @return
     */
    private static String createToken(String secret, Date expireTime, JwtAccount account) {
        return JWT.create().withAudience(String.valueOf(account.getId())).withIssuedAt(new Date()).withExpiresAt(expireTime).withClaim("accountName", account.getAccountName()).
                withClaim("nickName", account.getNickName()).withClaim("headerUrl", account.getHeaderUrl()).
                withClaim("attach", account.getAttach()).sign(Algorithm.HMAC256(secret));
    }

    /**
     * 检验合法性
     *
     * @param token  param
     * @param secret 秘钥
     * @throws MicroCommonException
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
        } catch (TokenExpiredException e) {
            //过期了
            throw MicroCommonException.USER_LOGIN_EXPIRE;
        } catch (Exception e) {
            //效验失败
            throw new MicroCommonException(MicroCommonException.USER_VERIFY_ERROR.getCode(), MicroCommonException.USER_VERIFY_ERROR.getMsg(), e);
        }
    }


    public static void main(String[] args) {
        JwtAccount account = new JwtAccount();
        account.setId(1L);
        account.setNickName("独孤怎会求败");
        account.setAccountName("xylx");
        account.setHeaderUrl("http://www.baidu.com");
        account.setAttach("abc");
        String token = createToken("test", account);
        System.out.println(token);
        JwtAccount c = verifyToken(token, "test");
        System.out.println(JSON.toJSON(c));
    }
}
