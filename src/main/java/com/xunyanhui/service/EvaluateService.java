/**
 * 创建日期：2017-1-4下午1:01:07
 * 修改日期：
 * 作者：邢传军
 */
package com.xunyanhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xunyanhui.model.ArtistEvaluateType;
import com.xunyanhui.model.ArtistPerforEvaluate;

public interface EvaluateService {

	/**
	 * 用于判断指定的用户对指定的对象是否进行了点赞或好评
	 * @param artistid				艺人id，该id与用户id相同
	 * @param objectid				被评价对象id
	 * @param evaluatetype			被评价对象的类型，1表示艺人，2表示作品或小样，3表示演艺，其他待扩充
	 * @return
	 */
	public Integer isGoodLevel(String objectid, String releaseid, int evaluatetype);
	
	/**
	 * 对指定的小样或作品/演艺/进行点赞或好评
	 * @param releaseid				评论人id，该评论人的用户id相同
	 * @param objectid				被评价对象id
	 * @param evaluatetype			被评价对象的类型，1表示艺人，2表示作品或小样，3表示演艺，其他待扩充
	 * @Param state					评价的状态值，1点赞/好评，0取消点赞/差评
	 * @return
	 */
	public Integer inGoodLevel(String objectid,String releaseid,int evaluatetype,int state);
	/**
	 * 对指定的小样或作品/演艺新建点赞或好评
	 * @param releaseid				评论人id，该评论人的用户id相同
	 * @param objectid				被评价对象id
	 * @param evaluatetype			被评价对象的类型，1表示艺人，2表示作品或小样，3表示演艺，其他待扩充
	 * @Param state					评价的状态值，1点赞/好评，0取消点赞/差评
	 * @return
	 */
	public Integer newGoodLevel(String id,String objectid, String releaseid,int evaluatetype,int state);
	/**
	 * 艺人完成演艺后是否接受到发布人的打分评价
	 * @param uid						被评论人id，
	 * @param performanceid				用户参加的演艺id
	 * @return
	 */
	/**
	 * 取指定的用户指定的小样或作品/演艺的点赞和好评情况
	 * @param releaseid				评论人id，该评论人的用户id相同
	 * @param objectid				被评价对象id
	 * @param evaluatetype			被评价对象的类型，1表示艺人，2表示作品或小样，3表示演艺，其他待扩充
	 * @return
	 */
	public Integer getGoodLevel(String objectid,String releaseid,int evaluatetype);
	
	public Integer isScore(String uid, String performanceid);
	/**
	 * 读取系统支持的用户可以打分评价项列表
	 * @return
	 */
	public List<ArtistEvaluateType> getScoreList();
	/**
	 * 读取艺人在指定演艺活动上得到的打分项
	 * @param artistid					艺人id，
	 * @param performanceid				用户参加的演艺id
	 * @return
	 */
	public List<ArtistPerforEvaluate> getScoreArtistOfPerfor(String artistid,String performanceid);
	/**
	 * 读取艺人在指定演艺活动上得到的评论
	 * @param artistid					艺人id，
	 * @param performanceid				用户参加的演艺id
	 * @param uid						发布评论的用户id
	 * @return
	 */
	public String getCommentArtistOfPerfor(String uid,String artistid,String performanceid);
	
	/**
	 * 对指定的小样或作品/演艺新建评论
	 * @param releaseid				评论人id，该评论人的用户id相同
	 * @param objectid				被评价对象id
	 * @param evaluatetype			被评价对象的类型，1表示艺人，2表示作品或小样，3表示演艺，其他待扩充
	 * @Param description			评价的内容
	 * @return
	 */
	 public Integer newCommentArtist(String id,String objectid,String releaseid,int evaluatetype,String description,String artistid);

	/**
	 * 写入艺人完成演艺后的打分值
	 * @param scoreList						打分评价的数据值
	 * @return
	 */
	public Integer addArtistPerforEvaluate(List<ArtistPerforEvaluate> scoreList);
	/**
	 * 读小样/作品的作者Id
	 * @param oid					作品或小样的id
	 * @return
	 */
	public String getOpusAuthor(String oid);
	
}
