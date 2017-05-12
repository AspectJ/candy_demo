package com.cp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期格式化工具
 * @author Administrator
 * @create 2015-11-25 上午10:33:48
 */
public class DateFormatUtil
{

	private static SimpleDateFormat yyyy_MM_dd_HH_mm_ss_format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static SimpleDateFormat yyyy_MM_dd_HH_mm_format= new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private static SimpleDateFormat yyyy_MM_dd_format= new SimpleDateFormat("yyyy-MM-dd");
	
	private static SimpleDateFormat HH_mm_format= new SimpleDateFormat("HH:mm");
	
	private static SimpleDateFormat YYYYMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 日期转换成 HH:mm 格式字符串
	 */
	public static String to_HH_mm_str(Date date){
		return HH_mm_format.format(date);
	}
	
	/**
	 * 字符串转换成 HH:mm 格式日期
	 */
	public static Date to_HH_mm_date(String dateStr) throws ParseException{
		return HH_mm_format.parse(dateStr);
	}
	
	/**
	 * 日期转换成 yyyy-MM-dd HH:mm:ss 格式字符串
	 */
	public static String to_yyyy_MM_dd_HH_mm_ss_str(Date date){
		return yyyy_MM_dd_HH_mm_ss_format.format(date);
	}
	
	/**
	 * Object对象转换成 yyyy-MM-dd HH:mm:ss 格式字符串
	 */
	public static String obj_to_yyyy_MM_dd_HH_mm_ss_str(Object object){
		return yyyy_MM_dd_HH_mm_ss_format.format(object);
	}
	
	/**
	 * 字符串转换成 yyyy-MM-dd HH:mm:ss 格式日期
	 */
	public static Date to_yyyy_MM_dd_HH_mm_ss_date(String dateStr) throws ParseException{
		return yyyy_MM_dd_HH_mm_ss_format.parse(dateStr);
	}
	
	/**
	 * 日期转换成 yyyy-MM-dd HH:mm 格式字符串
	 */
	public static String to_yyyy_MM_dd_HH_mm_str(Date date){
		return yyyy_MM_dd_HH_mm_format.format(date);
	}
	
	/**
	 * 字符串转换成 yyyy-MM-dd HH:mm 格式日期
	 */
	public static Date to_yyyy_MM_dd_HH_mm_date(String dateStr) throws ParseException{
		return yyyy_MM_dd_HH_mm_format.parse(dateStr);
	}
	
	/**
	 * 日期转换成 yyyy-MM-dd 格式字符串
	 */
	public static String to_yyyy_MM_dd_str(Date date){
		return yyyy_MM_dd_format.format(date);
	}
	
	/**
	 * 字符串转换成 yyyy-MM-dd 格式日期
	 */
	public static Date to_yyyy_MM_dd_date(String dateStr) throws ParseException{
		return yyyy_MM_dd_format.parse(dateStr);
	}
	
	/**
	 * 日期转换成 YYYYMMddHHmmss 格式字符串
	 */
	public static String to_YYYYMMddHHmmss_str(Date date){
		return YYYYMMddHHmmss.format(date);
	}
	
	
	/**
	 * 日期增加分钟
	 * @return
	 */
	public static Date addMinute(Date cur, int minute){
		Calendar cal = Calendar.getInstance();
		cal.setTime(cur);
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + minute);
		return cal.getTime();
	}
	
	/**
	 * 日期增加天数
	 * @return
	 */
	public static Date addDay(Date cur, int day){
		Calendar cal = Calendar.getInstance();
		cal.setTime(cur);
		cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + day);
		return cal.getTime();
	}
	
	
	//比较两个时间的大小
	public static int compareDate(String firstDate){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String secondDate = sDateFormat.format(new Date());
		try {
            Date dt1 = sDateFormat.parse(firstDate);
            Date dt2 = sDateFormat.parse(secondDate);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
	}
	
	//比较两个日期的相差天数
	public static long compareDateTime(String startTime,String endTime) throws ParseException{
		SimpleDateFormat smdf = new SimpleDateFormat( "yyyy-MM-dd");
		Date start = smdf.parse(startTime);
		Date end = smdf.parse(endTime);
		long t = (end.getTime() - start.getTime()) / (3600 * 24 * 1000);
		return t;
	}
	
	
	//比较两个日期时间的相差天数
	public static long compareDayTime(String startTime,String endTime) throws ParseException{
		SimpleDateFormat sd = new SimpleDateFormat( "yyyy-MM-dd HH:mm");
		long nd = 1000*24*60*60;//一天的毫秒数
		//获得两个时间的毫秒时间差异
		long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff/nd;//计算差多少天
		return day;
	}
	
	
	//比较两个日期时间的相差数
	public static String compareTime(String startTime,String endTime) throws ParseException{
		SimpleDateFormat sd = new SimpleDateFormat( "yyyy-MM-dd HH:mm");
		long nd = 1000*24*60*60;//一天的毫秒数
		long nh = 1000*60*60;//一小时的毫秒数
		long nm = 1000*60;//一分钟的毫秒数
		long ns = 1000;//一秒钟的毫秒数long diff;try {
		//获得两个时间的毫秒时间差异
		long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
		long day = diff/nd;//计算差多少天
		long hour = diff%nd/nh;//计算差多少小时
		long min = diff%nd%nh/nm;//计算差多少分钟
		long sec = diff%nd%nh%nm/ns;//计算差多少秒//输出结果
		return day+"天"+hour+"小时"+min+"分钟"+sec+"秒";
	}	
	
	
}
