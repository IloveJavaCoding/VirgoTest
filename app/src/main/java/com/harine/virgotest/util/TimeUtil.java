package com.harine.virgotest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author nepalese on 2021/3/26 15:51
 * @usage
 */
public class TimeUtil {
    public static final String DATE_FORMAT_TIME = "HH:mm:ss";
    public static final String DATE_FORMAT_TIME2 = "HH:mm";
    public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";

    /**
     * 仅时间格式时间戳
     * @return
     */
    public static long getCurTimeTime() {
        return getCurTime(DATE_FORMAT_TIME);
    }

    /**
     * 仅日期格式时间戳
     * @return
     */
    public static long getCurTimeDate() {
        return getCurTime(DATE_FORMAT_DATE);
    }

    /**
     * 指定格式时间戳
     * @param format
     * @return
     */
    public static long getCurTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        String curString = df.format(new Date());
        return string2LongTime(curString, format);
    }

    /**
     * 指定格式的当前时间、日期
     * @param format
     * @return
     */
    public static String getCurDate(String format){
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(new Date());
    }

    /**
     * 当前时间完整时间戳
     * @return
     */
    public static long getCurTime(){
        return System.currentTimeMillis();
    }

    public static long string2LongTime(String time, String format){
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        Date parse = null;
        try {
            parse = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse.getTime();
    }

    public static Date string2Date(String time, String format){
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        Date parse = null;
        try {
            parse = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    /**
     * 获取今日date: yyyy-MM-dd
     * @return
     */
    public static Date getToday(){
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_DATE, Locale.getDefault());
        String curString = df.format(new Date());
        Date parse = null;
        try {
            parse = df.parse(curString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    public static String formatTime(String time) {
        String hour = time.split(":")[0];
        if (Integer.parseInt(hour) < 10 && hour.length()<2) {
            return "0" + time;
        }
        return time;
    }
}
