/**
 * 创建日期：2016-12-6下午4:05:08
 * 修改日期：
 * 作者：邢传军
 */
package com.xunyanhui.message.quickin;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

public class GetMessageProperties {
	private String appId = "TxzlIyCcfS9KuENjjP4ux1";
	private String appKey = "rAnoicfrNX7915IxPocAL2";
	private String masterSecret = "KFDNBNKAVj9bgykwvqgeA5";
	private String appSecret;

	/*
	 * 使用构造方法加载参数
	 */
	public GetMessageProperties() {
		Properties prop = new Properties();
		try {
			// 读取属性文件a.properties
			
			InputStream in = this.getClass().getResourceAsStream("/getui.properties");
			prop.load(in); // /加载属性列表
			this.setAppId(prop.getProperty("appId"));
			this.setAppKey(prop.getProperty("appKey"));
			this.setMasterSecret(prop.getProperty("masterSecret"));
			this.setAppSecret(prop.getProperty("appSecret"));
			in.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the appKey
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * @param appKey
	 *            the appKey to set
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @return the masterSecret
	 */
	public String getMasterSecret() {
		return masterSecret;
	}

	/**
	 * @param masterSecret
	 *            the masterSecret to set
	 */
	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}

	/**
	 * @return the appSecret
	 */
	public String getAppSecret() {
		return appSecret;
	}

	/**
	 * @param appSecret the appSecret to set
	 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

}
