package com.xunyanhui.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xunyanhui.jms.bean.MessageInfo;
import com.xunyanhui.jms.service.JMSProducer;

@Controller
@RequestMapping("/")
public class JMSController {
	
	/** 日志实例 *//*
	private static final Logger logger = Logger.getLogger(JMSController.class);
	@Resource
	private JMSProducer jMSProducer;
	
	@Resource
	private ActiveMQQueue queueDestination;
	
	@Resource
	private ActiveMQQueue sessionAwareQueue;
	
	@Resource
	private ActiveMQQueue adapterQueue;
	
	@RequestMapping(value="")
	public String index(){
		return "JMS-producer";
	}
	
	@RequestMapping(value="/send.json")
	public String producerSendMessage(String message){
		Map<String,String> map = new HashMap<String,String>();
		map.put("type", "1");
		map.put("key", "value");
		message= "gfgfg";
		logger.info("fdfdfdfdf");
		logger.error("dfdfd");
		logger.debug("debug");
		jMSProducer.sendMessage(queueDestination, JSONObject.fromObject(map).toString());
		return "JMS-producer";
	}
	
	@RequestMapping(value="/sendsessionawaremessagelistenermessage")
	public String sessionAwareMessageListenerMessage(String sessionAwareMessageListenerMessage){
		jMSProducer.sendMessage(sessionAwareQueue, sessionAwareMessageListenerMessage);
		return "JMS-producer";
	}
	
	@RequestMapping(value="/sendmessagelisteneradapter")
	public String sendMessageListeneradApter(String messageListenerAdapter){
		jMSProducer.sendMessage(adapterQueue, messageListenerAdapter);
		return "JMS-producer";
	}
	public String producerSendMessage(MessageInfo message){
		jMSProducer.sendMessage(queueDestination, JSONObject.fromObject(message).toString());
		return "JMS-producer";
	}
	*/
}
