/**
 * 创建日期：2016-12-10上午11:35:04
 * 修改日期：
 * 作者：邢传军
 * 目的：用于生出消息体
 */
package com.xunyanhui.message;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgBody {
	//生成简单的，标准消息
	public static String  getSimple(String id,String msgtype){
		/*{标准的
			"title" : "用户名",
			"content" : "消息内容",
			"payload" : {
				"msgtype" : 1,
				"msginfo" : {
					"id" : "用户id",
					"time" : "12-10 11:21"
				}
			}
		}*/
		String sBody  = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		sBody =
				"{\"msgtype\":" +msgtype+",\"msginfo\":"+
						"{\"id\":\""+id+"\",\"time\":\""+df.format(new Date())+"\"}}";
				//"{\"id\":\""+id+"\",\"time\":\""+df.format(new Date())+"\"}";
		return sBody;
	}
	//生成复杂的，非标消息，主要是用户系统内私信在线状态
	public static String  getStandard(String id,String uname,String msgcontent,String msgtype){
		String sBody  = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

		sBody ="{\"msgtype\":" +msgtype+",\"msginfo\":"+
				"{\"id\":\""+id+"\",\"time\":\""+df.format(new Date())+"\",\"uname\":\""
		      +uname+"\",\"msgcontent\":\""+msgcontent+"\"}}";
		return sBody;
	}

}
