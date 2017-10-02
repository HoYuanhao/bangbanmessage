package com.xunyanhui.jms.jms;

public class JMSConsumerTwoMessageListenerAdapter {
	
	public void receiveMessage(String message){
		System.out.println("收到消息"+message);
	}
}
