package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.po.AccountPO;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.mapper.AccountMapper;
import com.ajie.commons.encrypt.EncryptUtil;
import com.ajie.commons.utils.RandomUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountHelper {
    /**
     * 生成随机用户名,用户名冲突会重复调用该方法5次
     *
     * @param count 当前重复次数
     * @return
     */
    protected static String genRandomAccountName(AccountMapper mapper, int count) {
        if (count == 5) {
            throw AccountException.USER_REGISTER_FAIL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("u_");
        String r = RandomUtil.getRandomString(16, true);
        sb.append(r);
        String name = sb.toString();
        //查询名字是否已存在
        AccountPO po = new AccountPO();
        po.setAccountName(name);
        AccountPO exist = mapper.selectOne(po.wrap(AccountPO.class));
        if (null == exist) {
            return name;
        }
        //重试
        return genRandomAccountName(mapper, ++count);
    }

    /**
     * 检查用户名合法性，只能包含字母数字和"_""-",纯数字也不行
     *
     * @param name
     * @return
     */
    public static boolean checkAccountName(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        boolean isNum = isNumeric(name);
        if (isNum) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c >= 'a' && c <= 'z') {
                continue;
            }
            if (c >= 'A' & c <= 'Z') {
                continue;
            }
            if (c >= '1' && c <= '9') {
                continue;
            }
            if (c == '-' || c == '_') {
                continue;
            }
            return false;
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 分析登录类型，1用户名，2手机号，3邮箱
     *
     * @param user
     * @return
     */
    public static int analyseUser(String user) {
        if (isNumeric(user)) {
            return 2;
        }
        if (user.indexOf("@") > -1) {
            return 3;
        }
        return 1;
    }

    /**
     * 处理密码，密码加盐（用户名）
     *
     * @param password 明文密码
     * @param salt     盐值（用户名）
     * @return
     */
    public static String encryptPassword(String password, String salt) {
        password += salt;
        password = EncryptUtil.sha256Encrypt(password);
        return password;
    }

    /**
     * 数据脱敏，一般是前保留2位后保留3位
     *
     * @return
     */
    public static String mask(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        StringBuilder sb = new StringBuilder();
        int length = content.length();
        if (length == 1) {
            //全打码
            return "*";
        }
        if (length == 2) {
            return "**";
        }
        if (length <= 5) {
            //除前后1位
            for (int i = 0; i < length; i++) {
                if (i == 0 || i == length - 1) {
                    sb.append(content.charAt(i));
                    continue;
                }
                sb.append("*");
            }
            return sb.toString();
        }
        for (int i = 0; i < length; i++) {
            if (i < 2 || i >= length - 3) {
                sb.append(content.charAt(i));
                continue;
            }
            sb.append("*");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
       /* boolean ret = checkAccountName("132");
        System.out.println(ret);*/
        System.out.println(mask("2"));
        System.out.println(mask("12"));
        System.out.println(mask("122"));
        System.out.println(mask("2344"));
        System.out.println(mask("21245"));
        System.out.println(mask("1234556"));
        System.out.println(mask("1234556@qq.com"));

        String p = encryptPassword("123", "xylx");
        System.out.println(p);


    }
}
