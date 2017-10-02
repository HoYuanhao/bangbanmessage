/**
 * 创建日期：2017-1-4下午1:02:27
 * 修改日期：
 * 作者：邢传军
 */
package com.xunyanhui.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xunyanhui.dao.EvaluateDao;
import com.xunyanhui.model.ArtistEvaluateType;
import com.xunyanhui.model.ArtistPerforEvaluate;
import com.xunyanhui.service.EvaluateService;

@Service
public class EvaluateServiceImpl implements EvaluateService {

	final Logger logger = Logger.getLogger("MyInfoDao");
	@Autowired
	private EvaluateDao evaluateDao;
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#isGoodLevel(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Integer isGoodLevel(String objectid, String releaseid, int evaluatetype) {
		// TODO Auto-generated method stub
		return evaluateDao.isGoodLevel(objectid, releaseid, evaluatetype);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#inGoodLevel(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Integer inGoodLevel(String objectid, String releaseid,
			int evaluatetype,int state) {
		// TODO Auto-generated method stub
		return evaluateDao.inGoodLevel(objectid, releaseid, evaluatetype,state);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#newGoodLevel(java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public Integer newGoodLevel( String id,String objectid, String releaseid,
			int evaluatetype, int state) {
		// TODO Auto-generated method stub
		return evaluateDao.newGoodLevel(id,objectid, releaseid, evaluatetype, state);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#isScore(java.lang.String, java.lang.String)
	 */
	@Override
	public Integer isScore(String uid, String performanceid) {
		// TODO Auto-generated method stub
		return evaluateDao.isScore(uid, performanceid);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#getScoreList()
	 */
	@Override
	public List<ArtistEvaluateType> getScoreList() {
		// TODO Auto-generated method stub
		return evaluateDao.getScoreList();
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#getScoreArtistOfPerfor(java.lang.String, java.lang.String)
	 */
	@Override
	public List<ArtistPerforEvaluate> getScoreArtistOfPerfor(String artistid,
			String performanceid) {
		// TODO Auto-generated method stub
		return evaluateDao.getScoreArtistOfPerfor(artistid, performanceid);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#getCommentArtistOfPerfor(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getCommentArtistOfPerfor(String uid, String artistid,
			String performanceid) {
		// TODO Auto-generated method stub
		return evaluateDao.getCommentArtistOfPerfor(uid, artistid, performanceid);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#getGoodLevel(java.lang.String, java.lang.String, int)
	 */
	@Override
	public Integer getGoodLevel(String objectid, String releaseid,
			int evaluatetype) {
		// TODO Auto-generated method stub
		return evaluateDao.getGoodLevel(objectid, releaseid, evaluatetype);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#newCommentArtist(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public Integer newCommentArtist(String id, String objectid,
			String releaseid, int evaluatetype, String description,String artistid) {
		// TODO Auto-generated method stub
		return evaluateDao.newCommentArtist(id, objectid, releaseid, evaluatetype, description,artistid);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#addArtistPerforEvaluate(java.util.List)
	 */
	@Override
	public Integer addArtistPerforEvaluate(List<ArtistPerforEvaluate> scoreList) {
		// TODO Auto-generated method stub
		return evaluateDao.addArtistPerforEvaluate(scoreList);
	}
	/* (non-Javadoc)
	 * @see com.xunyanhui.service.EvaluateService#getOpusAuthor(java.lang.String)
	 */
	@Override
	public String getOpusAuthor(String oid) {
		// TODO Auto-generated method stub
		return evaluateDao.getOpusAuthor(oid);
	}

}
