/**

 * 创建日期：2016-12-16下午3:20:41
 * 修改日期：
 * 作者：邢传军
 * 目的：用于系统消息处理
 */
package com.xunyanhui.service;

import java.util.List;

import com.xunyanhui.bean.ArtiArtist;
import com.xunyanhui.bean.UserPushInfo;
import com.xunyanhui.model.ArtistAnnouncement;
import com.xunyanhui.model.ArtistBiography;
import com.xunyanhui.model.ArtistOpus;
import com.xunyanhui.model.User;

/**
 * 对用户操作的业务方法
 * 
 * @author 柯鑫
 * 
 */
public interface UserService {

	public final static int LOGIN_USER_NAME_FAIL = 1;
	public final static int LOGIN_SUCCESS = 0;
	public final static int LOGIN_ERRO = -1;
	public final static int LOGIN_PASSWORD_FAIL = 2;
	
	public final static String LOGIN_USER = "user";


	/**
	 * 获取用户对应的cid与devicetoken
	 * @param id
	 *            要获取User的id
	 * @return 根据id取到的User 如果没有则返回值为Null
	 */
	public UserPushInfo getCid(String id);
	/**
	 * 根据id取用户信息
	 * 
	 * @param id
	 *            需要获取的用户id
	 * @return 返回用户的详细信息对象
	 */

	public User getUserById(String id);

	/**
	 * 登录业务方法
	 * 
	 * @param loginName
	 *            登录用户名
	 * @param passwd
	 *            登录密码
	 * @return
	 */
	public int login(String loginName, String passwd);

	/**
	 * 注册业务方法
	 * 
	 * @param user
	 *            注册的用户信息
	 * @return 是否注册成功
	 */
	public boolean regist(User user);

	/**
	 * 检测该手机号是否已经注册
	 * 
	 * @param phoneNum
	 *            需要检测的手机号
	 * @return 返回手机号是否注册 true 已经注册 false 没有注册
	 */
	public boolean hasPhoneNum(String phoneNum);

	public boolean hasUserName(String userName);

	


}
