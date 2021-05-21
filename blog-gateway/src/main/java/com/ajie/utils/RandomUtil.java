package com.ajie.commons.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    /**
     * 14位十进制的二进制全1十六进制，防止位数溢出
     */
    private static final long TIMESTAMP_MODE = 0xFFFFFFFFFFFL;

    /**
     * 生成36位随机字串，其中包含32位数字字母和4个-
     *
     * @return
     */
    public static String getRandomString_36() {
        StringBuilder sb = new StringBuilder();
        //获取9位随机数
        long randomIntFromRange = getRandomLongFromRange(1000000000L, 9999999999L);
        sb.append(randomIntFromRange);
        //13位
        long mill = System.currentTimeMillis();
        //打乱时间戳格式，方式别人看出来0x9184E729FFFL是
        long handleMill = (mill + getRandomLongFromRange(1000000000000L, 9999999999999L)) & TIMESTAMP_MODE;
        sb.append(handleMill);
        //获取3-5位英文字串
        int size = String.valueOf(getRandomLongFromRange(100, 99999)).length();
        StringBuilder charSb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            //ascii a-z
            char c = getRandomLowerChar();
            charSb.append(c);
        }
        //补全后面的数
        int offset = 32 - 13 - 10 - size;
        int remainMix = (int) Math.pow(10, offset - 1);
        int remainMax = (int) (Math.pow(10, offset) - 1);
        long remainNum = getRandomLongFromRange(remainMix, remainMax);
        sb.append(remainNum);
        //将字母随机插入
        int sbLength = sb.length();
        //已经插入字母的位置
        Set<Integer> set = new HashSet<>();
        int charSbIdx = 0;
        while (sb.length() < 32) {
            int poi = (int) getRandomLongFromRange(0, sbLength);
            if (set.contains(poi)) {
                continue;//该位置已经插入了
            }
            sb.insert(poi, charSb.charAt(charSbIdx++));

        }
        //插入分割线
        for (int i = 8; i < 32; i = (i + 8 + 1)) {
            sb.insert(i, "-");
        }
        return sb.toString();
    }

    /**
     * 生成[min,max]之间随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static long getRandomLongFromRange(long min, long max) {
        //+1是因为nextLong是开区间
        long i = ThreadLocalRandom.current().nextLong(max - min + 1);
        return min + i;
    }

    /**
     * 随机获取一个小写字母
     *
     * @return
     */
    public static char getRandomLowerChar() {
        return (char) getRandomLongFromRange(97, 122);
    }

    /**
     * 随机获取一个小写字母
     *
     * @return
     */
    public static char getRandomUpperChar() {
        return (char) getRandomLongFromRange(65, 90);
    }

    /**
     * 生成随机字串
     *
     * @param size         字串长度
     * @param isContainNum 是否包含数字，true
     * @return
     */
    public static String getRandomString(int size, boolean isContainNum) {
        if (0 >= size) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (isContainNum) {

        }
        for (int i = 0; i < size; i++) {
            sb.append(getRandomLowerChar());
        }
        if (isContainNum) {
            int numSize = (int) getRandomLongFromRange(1, size - 1);
            if (numSize > (size >> 1)) {
                //数字数量多于一般字母，数字减半
                numSize >>= 1;
            }
            for (int i = 0; i < numSize; i++) {
                //生成随机数
                String r = String.valueOf(getRandomLongFromRange(0, 9));
                //将随机数插入字符中
                int poi = (int) getRandomLongFromRange(0, size - 1);
                sb.setCharAt(poi, r.charAt(0));
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
      /*  Set<String> set = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                String s = getRandomString_36();
                set.add(s);
                //list.add(s);
            }).start();
        }
        Thread.sleep(3000);
        System.out.println(set.size());*/
        String random = getRandomString(16, true);
        System.out.println(random);
    }

}
