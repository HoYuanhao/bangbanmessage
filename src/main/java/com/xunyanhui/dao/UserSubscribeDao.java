package com.xunyanhui.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xunyanhui.bean.UserSubscribeV;
import com.xunyanhui.bean.UserSubscribeVS;
import com.xunyanhui.model.HomePic;

/**
 * 关于系统相关信息的Dao
 * 
 * @author 邢传军
 * 
 */
public interface UserSubscribeDao {
	/**
	 * 读取指定用户的演艺活动订阅情况
	 * @param uid
	 * @return
	 */
	public List<UserSubscribeV> selectUserSubscribeByUid(
			@Param("uid") String uid);
	/**
	 * 修改指定用户的指定演艺活动订阅情况
	 * @param uid
	 * @param pid
	 * @param substate
	 * @return
	 */
	public int updateUserSubscribeByUid(
			@Param("uid") String uid,
			@Param("pid") int pid,
			@Param("substate") int substate
			);
	/**
	 * 读取指定演艺活动订阅用户列表及其cid
	 * @param pid
	 * @return
	 */
	public List<UserSubscribeVS> selectSubscribeByPid(
			@Param("pid") int pid
			);

}
