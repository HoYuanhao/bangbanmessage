/**
 * 创建日期：2016-12-23下午10:12:21
 * 修改日期：
 * 作者：邢传军
 */
package com.xunyanhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;


import com.xunyanhui.bean.EvaluationOpus;
import com.xunyanhui.bean.MyArtistOpus;
import com.xunyanhui.bean.MyArtistOpusL;
import com.xunyanhui.bean.MyPerformance;
import com.xunyanhui.bean.MyPerformanceDetail;
import com.xunyanhui.bean.Performance;
import com.xunyanhui.bean.SignupArtist;
import com.xunyanhui.bean.SignupArtistOfPerf;
import com.xunyanhui.model.ArtPerformanceEntry;
import com.xunyanhui.model.ArtistAnnouncement;
import com.xunyanhui.model.EntryRecordN;

public interface MyPerformanceService {
	
	
	/**
	 * 判断某一用户是否是艺人的
	 * @param uid
	 * @return
	 */
	public int isArtist(String uid);
	/**
	 * 读取指定的用户参加的演艺活动，
	 * @param uid			用户id
	 * @return
	 */
	public List<ArtPerformanceEntry> getArtPerformanceEntryList(String uid);
	/**
	 * 读取指定的用户的小样/作品列表，
	 * @param uid			用户id
	 * @return
	 */
	public List<MyArtistOpus> getSelfOpusList(String uid);
	
	
	
	public MyArtistOpusL getSelfOpusById(String objectid);
	
	public List<EvaluationOpus> getPostOfSelfOpus(String objectid);
	
	public List<MyPerformance> getSendList(String uid);
	/**
	 * 获取用户发布的某一演艺活动详情
	 * @param uid					艺人id
	 * @param performanceid			演艺活动id
	 * @return
	 */
	public MyPerformanceDetail getPerformanceDetailsOfUser(String uid,String performanceid);
	/**
	 * 检查用户是否为某一演艺的发布人
	 * @param 				uid
	 * @param 				performanceid
	 */
	public int checkPerformanceOfUser(String uid,String performanceid);
	/**
	 * 检查用户是否为某一演艺的发布人
	 * @param 				artistid			对应报名的艺人id
	 * @param 				performanceid		演艺活动id
	 * @param 				state				演艺状态
	 */
	public int updateEntryState(String artistid,String performanceid,int state);
	/**
	 * 获取指定的报名的详情
	 * @param 				artistid报名艺人的id
	 * @param 				performanceid
	 */
	public EntryRecordN getEntryRecord(String artistid,String performanceid);
	
	public List<MyPerformance> getEnableList(String uid);
	
	/**
	 * 获取某一艺人的通告列表
	 * @param uid
	 * @return
	 */
	public List<ArtistAnnouncement> getAnnounceOfArtist( String uid);
	/**
	 * 取消指定艺人的某一通告
	 * @param uid			艺人id
	 * @param aid			通告id
	 * @return
	 */
	public int cancelAnnounce(String uid,String aid);
	
	public int newAnnounce(ArtistAnnouncement artistAnnouncement);
	
	/**
	 * 获取指定用户对某一艺人的关注情况
	 * @param uid				用户id
	 * @param oid				被关注艺人id
	 * @return
	 */
	public int getAttention(String uid,String oid);
	/**
	 * 指定用户对某一艺人的首次关注
	 * @param uid				艺人id
	 * @param oid				被关注艺人id
	 * @return
	 */
	public int newAttention(String uid,String oid,String uaid);
	/**
	 * 维护用户对某一艺人的关注状态
	 * @param uid				用户id
	 * @param oid				被关注艺人id
	 * @param uastate			关注状态    1		关注		0		取消
	 * @return
	 */
	public int updateAttention(String uid,String uaoid,int uastate);
	/**
	 * 获取某一演艺活动报名的艺人的id和艺名
	 * @param performanceid
	 * @return
	 */
	public List<SignupArtist> signupArtistOfPerf(String performanceid);
	/**
	 * 获取指定艺人发布某一演艺活动报名的艺人的详细信息
	 * @param performanceid
	 * @return
	 */
	public List<SignupArtistOfPerf> signupArtistOfPerfByUser(String performanceid,String uid);
	/**
	 * 更改演艺活动报名状态 
	 * @param uid			艺人id
	 * @param performanceId			演艺活动状态id
	 * @param signup_state			演艺活动报名状态1可以报名，0不可以报名
	 * @return
	 */
	
	public int updateSignUpState(String uid,String performanceId,int signup_state);
	/**
	 * 修改演艺报名的报价，前提是报名时未成交状态 
	 * @param artistid				艺人id
	 * @param performanceId			演艺活动状态id
	 * @param price					修改后的演艺活动报价
	 * @param description			修改后的演艺活动报价说明
	 * @return
	 */
	public int updatePrice(String artistid,String performanceId,int price,String description);


}
