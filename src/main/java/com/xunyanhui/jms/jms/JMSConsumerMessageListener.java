package com.xunyanhui.jms.jms;

import java.io.IOException;

import javax.jms.JMSException;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.IQueryResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.xunyanhui.bean.UserPushInfo;
import com.xunyanhui.jms.bean.MessageInfo;
import com.xunyanhui.message.MsgBody;
import com.xunyanhui.model.SystemMessage;

import com.xunyanhui.service.ArtistOpusService;
import com.xunyanhui.service.UserService;
import com.xunyanhui.service.SystemMessageService;

import com.xunyanhui.utils.ReturnString;
import com.xunyanhui.utils.UUidUtil;

public class JMSConsumerMessageListener implements MessageListener {
	/** 日志实例 */
	private static final Logger logger = Logger
			.getLogger(JMSConsumerMessageListener.class);

	@Autowired
	private SystemMessageService systemMessageService;
	@Autowired
	private UserService userService;
	@Autowired
	private ArtistOpusService artistOpusService;

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
				.println("----xunyanhui_Meaage on message receive11111111------------");

		try {

			String message_s = ((TextMessage) message).getText();
			System.out.println(message_s);
			JSONObject message_json = JSONObject.fromObject(message_s);
			MessageInfo messageInfo = (MessageInfo) JSONObject.toBean(
					message_json, MessageInfo.class);
			switch (messageInfo.getType()) {
			case 1:
				// 联系他，系统内私聊
				System.out.println("1");
				sendPirvateMsg(messageInfo.getSid(), messageInfo.getDid(),
						messageInfo.getValue(), messageInfo.getOption());
				break;
			case 7:
				// 用户被关注
				System.out.println("7");
				sendAttention(messageInfo.getSid(), messageInfo.getDid());
				break;
			case 8:
				System.out.println("8");
				updatePraise(messageInfo);
				break;

			}

			System.out.println("message===>"
					+ ((TextMessage) message).getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 联系他，发送系统内私聊 修改：原设置方案是每条聊天信息都通过个推发送，鉴于个推的到达率比较低，将聊天功能集成为环信
	 * 个推只负责发送聊天发起的通知消息
	 * 
	 * @param sid
	 *            发起私聊人
	 * @param did
	 *            接收私聊人
	 * @param value
	 *            私聊内容
	 */
	private void sendPirvateMsg(String sid, String did, String value,String uname) 
	{
		IGtPush push;
		push = this.getPush1();
		int user_status = 0;
		// 获取用户的cid
		UserPushInfo userPushInfo = userService.getCid(did);
		// 获取接受用户是否在线
		if (push == null) {
			System.out.println("创建个推链接失败！");
		} 
		else {
			if (userPushInfo.getCid().equals(userPushInfo.getDevicetoken())) {
				//android平台
				IQueryResult abc = push.getClientIdStatus(this.appId,
						userPushInfo.getCid());
				if (abc.getResponse().get("result").equals("Offline")) {
					// 离线
					user_status = 1;System.out.println("lixian");
				} else {
					user_status = 0;System.out.println("zaixian");
				}
				// 透传消息模板，根据用户状态发送标准和非标的模板信息
				TransmissionTemplate template = getUserTemplate(user_status,
						uname, value, sid);
				SingleMessage message = new SingleMessage();
				message.setOffline(true);
				// 离线有效时间，单位为毫秒，可选
				message.setOfflineExpireTime(24 * 3600 * 1000);
				message.setData(template);
				// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
				message.setPushNetWorkType(0);
				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(userPushInfo.getCid());
				// target.setAlias(Alias);
				IPushResult ret_getui = null;
				try {
					ret_getui = push.pushMessageToSingle(message, target);
					System.out.println("send result:"+ ret_getui.getResponse().get("result") + ","+ message.toString());
				} catch (RequestException e) {
					e.printStackTrace();
					ret_getui = push.pushMessageToSingle(message, target,e.getRequestId());
					logger.error("uname:" + uname + ",to:" + did + ",msg:"+ value + " error");
				}
				if (ret_getui != null
						&& ret_getui.getResponse().get("result").equals("ok")) {
					logger.info("uname:" + uname + ",to:" + did + ",msg:"+ value + " success");
				} else {
					logger.error("uname:" + uname + ",to:" + did + ",msg:"+ value + " failure,"+ ret_getui.getResponse().toString());
				}
			}
			else{
				System.out.println("ios");
			}
		}
		
	}

	/*
	 * 当用户被关注时想用户提示被关注
	 */
	private void sendAttention(String sid, String did) {
		IGtPush push;
		push = this.getPush1();
		ReturnString ret = new ReturnString();
		String msgId = UUidUtil.getUUid();
		SystemMessage systemMessage = new SystemMessage();
		systemMessage.setId(msgId);
		systemMessage.setAcceptId(did);
		systemMessage.setReleaseId(sid);
		systemMessage.setDetails("有用户关注了你！");
		systemMessage.setTopic("关注");
		systemMessage.setType(1);

		System.out.println(systemMessage.toString());
		try {
			System.out.println("systemMessageService"
					+ systemMessageService.toString());
			if (systemMessageService.newSystemMessage(systemMessage) == 1) {

				try {

					push.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("异常");
					e.printStackTrace();

				}

				UserPushInfo cid = userService.getCid(did);
				System.out.print("did:" + did + ",cid:" + cid.getCid());
				// 透传消息模板，根据自定义的应用协议，系统消息发同一的透传消息模板
				/*
				 * { "title" : "消息标题", "content" : "消息内容", "payload" : {
				 * "msgtype" : 2, "msginfo" : { "id" : "消息id", "time" :
				 * "12-10 11:21" } } }
				 */
				TransmissionTemplate template = getSystemTemplate(
						systemMessage.getTopic(), systemMessage.getDetails(),
						systemMessage.getId(), "2");

				SingleMessage message = new SingleMessage();
				message.setOffline(true);
				// 离线有效时间，单位为毫秒，可选
				message.setOfflineExpireTime(24 * 3600 * 1000);
				message.setData(template);
				// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
				message.setPushNetWorkType(0);
				Target target = new Target();
				target.setAppId(appId);
				target.setClientId(cid.getCid());
				// target.setAlias(Alias);
				IPushResult ret_getui = null;
				try {

					ret_getui = push.pushMessageToSingle(message, target);
					System.out.println("ret_getui" + ret_getui.toString() + ";"
							+ ret_getui.getResponse().get("result"));
				} catch (RequestException e) {
					e.printStackTrace();
					ret_getui = push.pushMessageToSingle(message, target,
							e.getRequestId());
				}
				if (ret_getui != null) {
					ret.setResult("success");
					systemMessageService.updateSysMsgState(systemMessage
							.getId());

				} else {
					ret.setResult("failure");

				}
			} else {
				ret.setResult("failure");
			}
		} catch (Exception e) {
			System.out.println(e.toString());

		}

	}

	/*
	 * 当用户作品/小样被点赞或演艺活动中的用户得到评论的时候执行
	 */
	private void updatePraise(MessageInfo messageInfo) {
		/*
		 * {"type":"8","sid":"sid","did":"did","value":"消息内容"}
		 * Sid表示被点赞/好评的小样id/艺人id Did 表示 1 点赞 2好评 Value 表示 0 取消点赞/差评 1点赞/好评
		 */
		try {
			if (messageInfo.getDid().equals("1")) {
				// 点赞
				if (messageInfo.getValue().equals("0")) {
					artistOpusService.updatePraise(messageInfo.getSid(), -1);
				} else {
					artistOpusService.updatePraise(messageInfo.getSid(), 1);
				}
			} else {
				if (messageInfo.getValue().equals("0")) {
					artistOpusService.updatePraise(messageInfo.getSid(), 0);
				} else {
					artistOpusService.updatePraise(messageInfo.getSid(), 1);
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}

	@SuppressWarnings("unused")
	private Boolean checkIsOnline(String uid) {
		IGtPush push;
		push = this.getPush1();
		Boolean isOnline = true;
		UserPushInfo cid = userService.getCid(uid);
		IQueryResult abc = push.getClientIdStatus(appId, cid.getCid());
		if (abc.getResponse().get("result").equals("Offline")) {
			isOnline = true;
		} else {
			isOnline = false;
		}
		return isOnline;
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

	/*
	 * 用于生成通知消息模板，title为通知消息的标题，text为通知消息的内容,transContent为通知消息的透传信息
	 */
	public static NotificationTemplate notificationTemplate(String title,
			String text, String transContent) {
		NotificationTemplate template = new NotificationTemplate();
		// 设置APPID与APPKEY
		template.setAppId(appId);
		template.setAppkey(appKey);
		// 设置通知栏标题与内容
		template.setTitle(title);
		template.setText(text);
		// 配置通知栏图标
		template.setLogo("icon.png");
		// 配置通知栏网络图标
		template.setLogoUrl("");
		// 设置通知是否响铃，震动，或者可清除
		template.setIsRing(true);
		template.setIsVibrate(true);
		template.setIsClearable(true);
		// 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		template.setTransmissionType(1);
		template.setTransmissionContent(transContent);
		// 设置定时展示时间
		// template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
		return template;
	}

	/**
	 * @return the push1
	 */
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
