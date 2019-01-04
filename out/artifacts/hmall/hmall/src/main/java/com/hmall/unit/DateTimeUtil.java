package com.hmall.unit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
//时间格式转换，转换成字符串或者字符串转换成时间
public class DateTimeUtil {


    public static final String STANDRAD_FORMAT="yyyy-mm-dd HH:MM:SS";
    public static Date strToDate(String dateTimestr,String formatstr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(formatstr);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimestr);
        return dateTime.toDate();
    }

    public static String dateTostr(Date date,String formatstr){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
       return dateTime.toString(formatstr);
    }

    public static Date strToDate(String dateTimestr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDRAD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dateTimestr);
        return dateTime.toDate();
    }

    public static String dateTostr(Date date){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDRAD_FORMAT);
    }
}
