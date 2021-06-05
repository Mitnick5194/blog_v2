package com.ajie.commons.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * 日期工具
 */
public class DateUtil {
    /**
     * 一小时的毫秒数
     */
    public static final long MILLI_OF_HOUR = 60 * 60 * 1000;
    /**
     * 一天的毫秒数
     */
    public static final long MILLI_OF_DAY = 24 * MILLI_OF_HOUR;

    public static Date localDate2Date(LocalDate localDate) {
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime date2LocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate date2LocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 指定日期加上毫秒数
     *
     * @param date
     * @param mills
     * @return
     */
    public static Date plusMills(Date date, long mills) {
        LocalDateTime localDate = date2LocalDateTime(date);
        localDate = localDate.plus(mills, ChronoUnit.MILLIS);
        return localDateTime2Date(localDate);
    }

    /**
     * 单位数字前面加个0
     *
     * @return
     */
    public static String prettyNum(int num) {
        if (num > 9 || num < 0) {
            return String.valueOf(num);
        }
        return "0" + num;
    }

    public static void main(String[] args) {
        Date d = plusMills(new Date(), MILLI_OF_HOUR);
        System.out.println(d);
    }


}
