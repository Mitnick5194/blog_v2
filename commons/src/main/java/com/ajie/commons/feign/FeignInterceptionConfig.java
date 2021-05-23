package com.ajie.commons.feign;

import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.interception.AbstractAuthInterceptor;
import com.ajie.commons.utils.ServerUtil;
import com.ajie.commons.utils.UserInfoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

/**
 * feign调用添加全局的参数
 */
public class FeignInterceptionConfig implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {
        //requestTemplate.query("reqId", MDC.get("reqId"));
        //reqId改成放入头部
        String reqId = MDC.get("reqId");
        if (null == reqId) {
            reqId = "";
        }
        requestTemplate.header("reqId", reqId);
        Long userId = UserInfoUtil.getUserId();
        String userName = UserInfoUtil.getUserName();
        if (null == userId && AbstractAuthInterceptor.AUTH_TEST_KEY.equals(userName) && ServerUtil.isDev()) {
            //测试调用
            requestTemplate.header(AbstractAuthInterceptor.TICKET_KEY, AbstractAuthInterceptor.TEST_HEADER);
        } else {
            String userHeader = UserInfoUtil.getUserHeader();
            JwtAccount account = new JwtAccount();
            account.setId(userId);
            account.setNickName(userName);
            account.setHeaderUrl(userHeader);
            requestTemplate.header(AbstractAuthInterceptor.TICKET_KEY, JSON.toJSONString(account, SerializerFeature.WriteMapNullValue));
        }

    }
}
