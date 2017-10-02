package com.xunyanhui.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xunyanhui.model.Artist;
import com.xunyanhui.model.ArtistDetailView;
import com.xunyanhui.model.AuthFile;
import com.xunyanhui.model.Coupon;
import com.xunyanhui.model.Employer;
import com.xunyanhui.model.User;

public interface ArtistDao {



	/**
	 * 根据id取出艺人信息,当前用户登录的
	 * 
	 * @param id
	 *            需要取出艺人的id
	 * @return 取出的艺人对象
	 */
	public ArtistDetailView getAtristById(
			@Param("id") String id,
			@Param("uid") String uid);
	
	/**
	 * 根据id取出艺人信息，当前用户未登录
	 * 
	 * @param id
	 *            需要取出艺人的id
	 * @return 取出的艺人对象
	 */
	public ArtistDetailView getAtristByIdUnLogin(
			@Param("id") String id,
			@Param("uid") String uid);
	
	/**
	 * @param uaid				关注id
	 * @param type	
	 * @param oid
	 * @param uid
	 * @return
	 */
	public int addAttention(
			@Param("uaid") String uaid,
			@Param("type") int type,
			@Param("oid") String oid,
			@Param("uid") String uid
			);

}
	
