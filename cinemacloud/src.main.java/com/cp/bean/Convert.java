package com.cp.bean;

import java.text.DecimalFormat;


/**
 * 类型转换
 * @author stone 2015-4-23下午6:16:54
 */
public class Convert
{
	
	/**
	 * 保留两位小数
	 */
	public static String saveDecimalTwo(Object value){
		DecimalFormat fnum = new  DecimalFormat("##0.00");
		return fnum.format(value);
	}
	
	/**
	 * 保留一位小数
	 */
	public static String saveDecimalOne(Object value){
		DecimalFormat fnum = new  DecimalFormat("##0.0");
		return fnum.format(value);
	}
	
	
	
}