package com.xunyanhui.controller;



import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.base.payload.APNPayload.SimpleAlertMsg;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.xunyanhui.message.MsgBody;
import com.xunyanhui.service.SmsSendService;

/**
 * 包含常用测试方法Controler
 * 
 */
@Controller
@RequestMapping("/")
public class TestController {

	final Logger logger = Logger.getLogger("TestController");
	@Resource
	SmsSendService smsSendService;

	static String appId = "XvuSPtGYZG9TNPHUCYzeq";
	static String appKey = "uhQmVpUzud7wxZDQ7IT158";
	static String masterSecret = "CiBEowTZok8nzVZOQzT0u3";
	static String appSecret = "Bl3IzkDD5c9VV8OWqNaxDA";

	private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
	private IGtPush push1;

	/*
	 * 用于测试服务器的启动状态
	 */
	@RequestMapping(value = "test", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String test(HttpServletRequest request) {
		String ret = "{\"status\":\"success\"}";
		return ret;
	}

	/*
	 * 用于测试服务器的启动状态
	 */
	@RequestMapping(value = "sendSMS/{mobile}/{content}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String sendSMS(@PathVariable("mobile") String mobile,
			@PathVariable("content") String content, HttpServletRequest request) {
		String ret;
		if (smsSendService.sendNotify(mobile, content)) {
			ret = "{\"status\":\"success\"}";
		} else {
			ret = "{\"status\":\"fail\"}";
		}

		return ret;
	}

	/*
	 * 测试ios端的个推推送
	 */
	@RequestMapping(value = "sendMessageIos/{type}/{devicetoken}/{cid}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String sendMessageIos(@PathVariable("type") int type,@PathVariable("devicetoken") String devicetoken,
			@PathVariable("cid") String cid, HttpServletRequest request) {
		String ret="";
		IGtPush push;
		push = this.getPush1();
		// 获取接受用户是否在线
		if (push == null) {
			System.out.println("创建个推链接失败！");
		} else {
			if(type==1){
				//发送标准的消息
				TransmissionTemplate template = new TransmissionTemplate();
				template.setAppId(appId);
				template.setAppkey(appKey);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				String content = "{\"title\":\"title\",\"content\":\"content\",\"payload\":{\"msgtype\":1,\"msginfo\":{\"id\":\"id\",\"time\":\""+df.format(new Date())+"\"}}}";
				System.out.println(content);
				template.setTransmissionContent(content);
				template.setTransmissionType(2);
				APNPayload payload = new APNPayload();
				payload.setBadge(1);
				payload.setContentAvailable(1);
				payload.setSound("default");
				payload.setCategory("$由客户端定义");
				payload.addCustomMsg("payload", content);
				// 简单模式APNPayload.SimpleMsg
				SimpleAlertMsg simpleAlertMsg = new APNPayload.SimpleAlertMsg("hello");
				payload.setAlertMsg(simpleAlertMsg);
				template.setAPNInfo(payload);
				SingleMessage message = new SingleMessage();
				message.setOffline(true);
				// 离线有效时间，单位为毫秒，可选
				message.setOfflineExpireTime(24 * 3600 * 1000);
				message.setData(template);
				// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
				message.setPushNetWorkType(0);
				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(cid);
				IPushResult ret0 = push.pushAPNMessageToSingle(appId, devicetoken, message);
				ret = "{\"status\":\""+ret0.getResponse().get("result").toString()+"\"}";
			}else{
				//发送非标准的数据包
				TransmissionTemplate template = new TransmissionTemplate();
				template.setAppId(appId);
				template.setAppkey(appKey);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				String content = "{\"msgtype\":1,\"msginfo\":{\"id\":\"id\",\"time\":\""+
						df.format(new Date())+"\",\"uname\":\"uname\",\"msgcontent\":\"msgcontent\"}}";
				System.out.println(content);
				template.setTransmissionContent(content);
				template.setTransmissionType(2);
				APNPayload payload = new APNPayload();
				payload.setBadge(1);
				payload.setContentAvailable(1);
				payload.setSound("default");
				payload.setCategory("$由客户端定义");
				payload.addCustomMsg("payload", content);
				// 简单模式APNPayload.SimpleMsg
				payload.setAlertMsg(new APNPayload.SimpleAlertMsg("hello"));
				template.setAPNInfo(payload);
				SingleMessage message = new SingleMessage();
				message.setOffline(true);
				// 离线有效时间，单位为毫秒，可选
				message.setOfflineExpireTime(24 * 3600 * 1000);
				message.setData(template);
				// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
				message.setPushNetWorkType(0);
				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(cid);
				IPushResult ret0 = push.pushAPNMessageToSingle(appId, devicetoken, message);
				ret = "{\"status\":\""+ret0.getResponse().get("result")+"\"}";
			}
			
		}

		return ret;
	}
	/*
	 * 测试android端的个推推送
	 */
	@RequestMapping(value = "sendMessageAndroid/{type}/{devicetoken}/{cid}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String sendMessageAndroid(@PathVariable("type") int type,@PathVariable("devicetoken") String devicetoken,
			@PathVariable("cid") String cid, HttpServletRequest request) {
		String ret="";
		IGtPush push;
		push = this.getPush1();
		// 获取接受用户是否在线
		if (push == null) {
			System.out.println("创建个推链接失败！");
		} else {
			TransmissionTemplate template = new TransmissionTemplate();
			template.setAppId(appId);
			template.setAppkey(appKey);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			String content = null;
			if(type==1){
				//标准的数据格式
				content =	"{\"title\":\"title\",\"content\":\"content\",\"payload\":{\"msgtype\":1,\"msginfo\":{\"id\":\"id\",\"time\":\""+df.format(new Date())+"\"}}}";
			}else{
				//非标准的数据格式
				content = "{\"payload\":"
						+ "{\"msgtype\":1,\"msginfo\":{\"id\":\"id\",\"time\":\""+
						df.format(new Date())+"\",\"uname\":\"uname\",\"msgcontent\":\"msgcontent\"}}}";
			}
			System.out.println(content);
			template.setTransmissionContent(content);
			template.setTransmissionType(2);
			SingleMessage message = new SingleMessage();
			message.setOffline(true);
			// 离线有效时间，单位为毫秒，可选
			message.setOfflineExpireTime(24 * 3600 * 1000);
			message.setData(template);
			// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
			message.setPushNetWorkType(0);
			Target target = new Target();
			target.setAppId(appId);
			target.setClientId(cid);
			IPushResult ret0 = push.pushMessageToSingle(message, target);
			ret = "{\"status\":\""+ret0.getResponse().get("result")+"\"}";
		}

		return ret;
	}

	public IGtPush getPush1() {
		try {
			if (push1 == null) {
				push1 = new IGtPush(host, appKey, masterSecret);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return push1;
	}

	/**
	 * @param push1
	 *            the push1 to set
	 */
	public void setPush1(IGtPush push1) {
		this.push1 = push1;
	}

}
