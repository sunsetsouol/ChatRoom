package org.example.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TimeUtil {

    /**默认的时间格式*/
    private static final String LOCAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**默认的日期格式*/
    private static final String LOCAL_DATE = "yyyy-MM-dd";

    /**默认的时间格式正则表达式*/
    private static final String LOCAL_DATE_TIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";

    /**默认的日期格式正则表达式*/
    private static final String LOCAL_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    /**
     * 默认的时间格式校验器
     */
    private static final Pattern R_LOCAL_DATE_TIME = Pattern.compile(LOCAL_DATE_TIME_PATTERN);

    /**
     * 默认的日期格式校验器
     */
    private static final Pattern R_LOCAL_DATE = Pattern.compile(LOCAL_DATE_PATTERN);

    /**
     * 时间格式转换器
     */
    private static DateTimeFormatter formatter;

    private static DateTimeFormatter getFormatter(String formatStr) {
        return DateTimeFormatter.ofPattern(formatStr);
    }

    private static <T> String allTypeToTimeStr(T time, Class<T> timeClazz) {
        if (String.class.equals(timeClazz)) {
            return (String) time;
        } else if (LocalDateTime.class.equals(timeClazz)) {
            return localDateTimeToTimeStr((LocalDateTime) time);
        } else if (LocalDate.class.equals(timeClazz)) {
            return timeStrToDateTimeStr(((LocalDate) time).format(getFormatter(LOCAL_DATE)));
        } else if (Long.class.equals(timeClazz)) {
            LocalDateTime localDateTime = LocalDateTime.ofEpochSecond((Long) time, 0, ZoneOffset.ofHours(8));
            return localDateTimeToTimeStr(localDateTime);
        } else {
            throw new RuntimeException("传入的时间类型错误!");
        }
    }

    public static <T> T transfer(String time, Class<T> targetTimeClazz) {
        String timeStr = allTypeToTimeStr(time, String.class);
        return transferIn(timeStr, targetTimeClazz);
    }

    public static <T> T transfer(Long time, Class<T> targetTimeClazz) {
        String timeStr = allTypeToTimeStr(time, Long.class);
        return transferIn(timeStr, targetTimeClazz);
    }

    private static <T> T transferIn(String timeStr,Class<T> targetTimeClazz) {
        checkTimeFormat(timeStr);

        timeStr = timeStrToDateTimeStr(timeStr);
        if (LocalDateTime.class.equals(targetTimeClazz)) {
            return (T) timeStrToLocalDateTime(timeStr);
        } else if (LocalDate.class.equals(targetTimeClazz)) {
            return (T) timeStrToLocalDate(timeStr);
        } else if (Long.class.equals(targetTimeClazz)) {
            return (T) timeStrToLong(timeStr);
        } else if (Date.class.equals(targetTimeClazz)) {
            return (T) timeStrToDate(timeStr);
        } else if (String.class.equals(targetTimeClazz)) {
            return (T) timeStr;
        } else {
            throw new RuntimeException("只可以转换为LocalDateTime、Long、Date、LocalDate、String类型!");
        }
    }


    public static <T> T transfer(LocalDateTime time, Class<T> targetTimeClazz) {
        String timeStr = allTypeToTimeStr(time, LocalDateTime.class);
        return transferIn(timeStr, targetTimeClazz);
    }

    public static <T> T transfer(LocalDate time, Class<T> targetTimeClazz) {

        String timeStr = allTypeToTimeStr(time, LocalDate.class);

        return transferIn(timeStr, targetTimeClazz);
    }


    public static <T> T transfer(Date time, Class<T> targetTimeClazz) {

        String timeStr = allTypeToTimeStr(time, Date.class);

        return transferIn(timeStr, targetTimeClazz);
    }

    private static void checkTimeFormat(String time) {
        //首先匹配是否为正确的字符串
        Matcher m = R_LOCAL_DATE_TIME.matcher(time);
        Matcher n = R_LOCAL_DATE.matcher(time);
        if (!(m.find() || n.find())) {
            throw new RuntimeException("时间格式错误!");
        }
    }

    private static String dateToTimeStr(Date time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(LOCAL_DATE_TIME);
        return dateFormat.format(time);
    }


    private static String localDateTimeToTimeStr(LocalDateTime time) {
        formatter = getFormatter(LOCAL_DATE_TIME);
        return formatter.format(time);
    }


    private static Date timeStrToDate(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(LOCAL_DATE_TIME);
        try {
            return dateFormat.parse(time);
        } catch (ParseException e) {
            throw new RuntimeException("时间转换错误!");
        }
    }

    private static String timeStrToDateTimeStr(String time) {
        Matcher m = R_LOCAL_DATE_TIME.matcher(time);
        if (!m.find()) {
            return time + " 00:00:00";
        }
        return time;
    }

    private static Long localDateTimeToLong(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    private static LocalDateTime timeStrToLocalDateTime(String time) {
        return LocalDateTime.parse(time, getFormatter(LOCAL_DATE_TIME));
    }

    private static LocalDate timeStrToLocalDate(String time) {
        return timeStrToLocalDateTime(time).toLocalDate();
    }

    private static Long timeStrToLong(String time) {
        return localDateTimeToLong(timeStrToLocalDateTime(time));
    }

}
