package com.xunyanhui.jms.jms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.xunyanhui.bean.UserSubscribeVS;
import com.xunyanhui.jms.bean.MessageInfo;
import com.xunyanhui.message.MsgBody;
import com.xunyanhui.model.SystemMessage;

import com.xunyanhui.service.ArtistOpusService;
import com.xunyanhui.service.UserService;
import com.xunyanhui.service.SystemMessageService;
import com.xunyanhui.service.UserSubscribeService;

import com.xunyanhui.utils.ReturnString;
import com.xunyanhui.utils.UUidUtil;

public class JMSConsumerTwoMessageListener implements MessageListener {
	/** 日志实例 */
	private static final Logger logger = Logger
			.getLogger(JMSConsumerTwoMessageListener.class);

	@Autowired
	private UserSubscribeService userSubscribeService;
	@Autowired
	private SystemMessageService systemMessageService;

	static String appId = "XvuSPtGYZG9TNPHUCYzeq";
	static String appKey = "uhQmVpUzud7wxZDQ7IT158";
	static String masterSecret = "CiBEowTZok8nzVZOQzT0u3";
	static String appSecret = "Bl3IzkDD5c9VV8OWqNaxDA";

	private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
	
	private IGtPush push1;
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		System.out
				.println("----xunyanhui_Meaage on message receive222222222------------");

		try {

			String message_s = ((TextMessage) message).getText();
			System.out.println(message_s);
			JSONObject message_json = JSONObject.fromObject(message_s);
			MessageInfo messageInfo = (MessageInfo) JSONObject.toBean(
					message_json, MessageInfo.class);
			switch (messageInfo.getType()) {
			case 3:
				// 发布演艺后将为订阅该类别的的艺人推送提醒消息
				/*
				 * {"type":"3","sid":"sid","did":"did","value":"消息内容"} Sid表示演艺id
				 * did表示类别，value表示演艺标题
				 */
				System.out.println("3");
				sendPerforSub(messageInfo.getSid(), messageInfo.getDid(),
						messageInfo.getValue());
				break;

			}

			System.out.println("message===>"
					+ ((TextMessage) message).getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 当某一演艺活动发布后，向订阅了该类别演艺活动的用户推送消息 pid 演艺活动id ptype 演艺活动类型 title 演艺活动标题
	 */
	private void sendPerforSub(String pid, String ptype, String title) {
		IGtPush push;
		push = this.getPush1();
		ReturnString ret = new ReturnString();
		String msgId;
		Integer i_ptype = Integer.valueOf(ptype);
		List<UserSubscribeVS> userSubscribe = userSubscribeService
				.selectSubscribeByPid(i_ptype);
		// 配置推送目标
		List<Target> targets = new ArrayList<Target>();
		SystemMessage systemMessage = new SystemMessage();
		for (UserSubscribeVS attribute : userSubscribe) {
			System.out.println(attribute);
			Target target1 = new Target();
			target1.setAppId(appId);
			target1.setClientId(attribute.getCid());
			targets.add(target1);
			msgId = UUidUtil.getUUid();
			systemMessage.setId(msgId);
			systemMessage.setAcceptId(attribute.getUid());
			systemMessage.setReleaseId("1");
			systemMessage.setDetails(title);
			systemMessage.setTopic("演艺订阅");
			systemMessage.setType(5);
			systemMessageService.newSystemMessage(systemMessage);
		}
		// 透传消息模板，根据自定义的应用协议，系统消息发同一的透传消息模板
		/*
		 * { "title" : "消息标题", "content" : "消息内容", "payload" : { "msgtype" : 2,
		 * "msginfo" : { "id" : "消息id", "time" : "12-10 11:21" } } }
		 */
		TransmissionTemplate template = getSystemTemplate("演艺订阅", title, "id",
				"2");
		ListMessage message = new ListMessage();
		message.setData(template);
		// 设置消息离线，并设置离线时间
		message.setOffline(true);
		// 离线有效时间，单位为毫秒，可选
		message.setOfflineExpireTime(24 * 1000 * 3600);
		// taskId用于在推送时去查找对应的message
		String taskId = push.getContentId(message);
		if (targets != null && targets.size() != 0) {
			@SuppressWarnings("unchecked")
			IPushResult ret_send = push.pushMessageToList(taskId, targets);
			System.out.println(ret_send.getResponse().toString());
		} else {
			System.out.println("未发送，无订阅用户！");
		}
	}

	/*
	 * 设置发送的个人消息发送模板
	 */
	public TransmissionTemplate getUserTemplate(int user_status, String title,
			String content, String uid) {
		TransmissionTemplate template = new TransmissionTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		String content1;
		if (user_status == 0) {
			// 在线发送非标的
			System.out.println("zaixian");
			content1 = "{\"payload\":"
					+ MsgBody.getStandard(uid, title, content, "1") + "}";

		} else {
			// 离线发送的标准
			content1 = "{\"title\":\"" + title + "\",\"content\":\"" + content
					+ "\",\"payload\":{" + MsgBody.getSimple(uid, "1") + "}}";
			System.out.println("lixian");
		}
		logger.info(content1);
		System.out.println(content1);
		template.setTransmissionContent(content1);
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
		return template;
	}

	/*
	 * 设置发送的系统消息发送模板
	 */
	public TransmissionTemplate getSystemTemplate(String title, String content,
			String msgId, String msgType) {
		TransmissionTemplate template = new TransmissionTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(2);
		String content1;
		content1 = "{\"title\":\"" + title + "\",\"content\":\"" + content
				+ "\",\"payload\":" + MsgBody.getSimple(msgId, msgType) + "}";
		System.out.println(content1);
		template.setTransmissionContent(content1);
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
		return template;
	}
	/**
	 * @return the push1
	 */
	public IGtPush getPush1() {
		if(push1==null){
			push1 = new IGtPush(host, appKey, masterSecret);
		}
		return push1;
	}
	/**
	 * @param push1 the push1 to set
	 */
	public void setPush1(IGtPush push1) {
		this.push1 = push1;
	}

}
