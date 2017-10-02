/**
 * 创建日期：2017-1-13下午4:34:41
 * 修改日期：处理时间
 * 作者：邢传军
 */
package com.xunyanhui.utils;

import java.util.Date;

public class DateUtil{
	
	/*
	 * 判断local是否在start与end之间,时间类型
	 */
	public static boolean isInclude(Date start,Date end,Date local){
		boolean ret = false;
		if (start.getTime() <= local.getTime()&&end.getTime() >= local.getTime()) {
			ret = true;
        } 
		return ret;
	}
/*	
	 * 判断local是否在start与end之间,时间类型
	 
	public static boolean isInclude(String start,String end,String local){
		boolean ret = false;
		if (start.getTime() <= local.getTime()&&end.getTime() >= local.getTime()) {
			ret = true;
        } 
		return ret;
	}*/
}
