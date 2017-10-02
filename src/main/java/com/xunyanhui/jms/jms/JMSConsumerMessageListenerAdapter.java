package com.xunyanhui.jms.jms;

public class JMSConsumerMessageListenerAdapter {
	
	public void receiveMessage(String message){
		System.out.println("收到消息"+message);
	}
}
