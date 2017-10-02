package com.xunyanhui.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xunyanhui.bean.PerfBean;
import com.xunyanhui.bean.PerforBean;
import com.xunyanhui.bean.SignupArtist;

import com.xunyanhui.dao.MyPerformanceDao;
import com.xunyanhui.dao.PerformanceDao;
import com.xunyanhui.model.Artist;

import com.xunyanhui.model.Performance;
import com.xunyanhui.model.User;
import com.xunyanhui.service.PerformanceService;
import com.xunyanhui.service.UserService;
import com.xunyanhui.utils.UUidUtil;

@Service
public class PerformanceServiceImpl implements PerformanceService {
	@Autowired
	private PerformanceDao performanceDao;
	@Autowired
	private MyPerformanceDao myPerformanceDao;

	private static int PAGE_COUNT = 5;

	@Override
	public List<PerforBean> getPerformanceList(Date date, int order,
			String type, int page, String uid) {
		List<Performance> performanceList = performanceDao.getPerformanceList(
				date, order, type, (page - 1) * PAGE_COUNT, PAGE_COUNT);
		List<PerforBean> perList = new ArrayList<PerforBean>();
		PerforBean p = null;
		for (Performance performance : performanceList) {
			p = new PerforBean();
			p.setId(performance.getId());
			p.setAddress(performance.getAddress());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			String time = dateFormat.format(performance.getBeginTime());
			p.setTime(time);
			p.setPicPath(performance.getPic());
			p.setSalary(performance.getSalary() + "");
			p.setSignupNum(performance.getSignupNum());
			p.setSignupState(performance.getSignupState());


			p.setTitile(performance.getTitle());
			perList.add(p);
		}
		return perList;
	}

	@Override
	public boolean addPerformance(PerfBean perfBean,String pid,String pic) {
		// TODO Auto-generated method stub
		Performance p = new Performance();
		p.setId(pid);
		p.setAddress(perfBean.getAddress());
		p.setAuditState(IN_REVIEW);
		p.setContacts(perfBean.getContacts());
		p.setContactway(perfBean.getContactway());
		p.setCity(perfBean.getCity());
		p.setEndTime(new Date(perfBean.getEndTime()));
		p.setGender(perfBean.getGender());
		p.setUid(perfBean.getUid());
		p.setLanguage(perfBean.getLanguage());
		p.setNumberOfApplicants(perfBean.getArtistNum());
		p.setPerformBeginTime(new Date(perfBean.getPerformBeginTime()));
		p.setPerformEndTime(new Date(perfBean.getPerformEndTime()));
		p.setReleaseTime(perfBean.getReleaseTime());
		p.setRequire(perfBean.getRequire());
		p.setSalary(perfBean.getSalary());
		p.setSalaryDescription(perfBean.getSalaryDescription());
		p.setSignupState(1);
		p.setSignupNum(0);
		p.setBeginTime(new Date(perfBean.getBeginTime()));
		// p.setStateDescription(stateDescription)
		p.setStature(perfBean.getStature());
		p.setTitle(perfBean.getTitle());
		p.setType(Integer.valueOf(perfBean.getType()).toString());
		p.setPic(pic);
		p.setHomePic(pic);
		p.setLat(perfBean.getLat());
		p.setLng(perfBean.getLng());
		performanceDao.addPerformance(p);
		try {
			// performanceDao.addPerformance(p);
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return true;
	}

	@Override
	public com.xunyanhui.bean.Performance getPerformance(String perforId,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		com.xunyanhui.bean.Performance perfor = new com.xunyanhui.bean.Performance();
		perfor.setSelf(false);
		if (user != null) {
			if (performanceDao.hasPerforByIdAndUid(perforId, user.getId()) > 0) {
				perfor.setSelf(true);
			}

		}
		Performance performance = performanceDao
				.getPerformanceDetails(perforId);
		perfor.setCity(performance.getAddress());
		perfor.setContacts(performance.getContacts());
		perfor.setContactway(performance.getContactway());
		String gender = null;
		if (performance.getGender() == 0) {
			gender = "不限";
		} else if (performance.getGender() == 1) {
			gender = "女";
		} else if (performance.getGender() == 2) {
			gender = "男";
		}
		perfor.setGender(gender);
		perfor.setId(performance.getId());
		perfor.setLanguage(performance.getLanguage());
		perfor.setNumberOfApplicants(performance.getNumberOfApplicants());
		perfor.setRequire(performance.getRequire());
		perfor.setSalary(performance.getSalary().toString());
		perfor.setSalaryDescription(performance.getSalaryDescription());
		perfor.setSignupNum(performance.getSignupNum());
		perfor.setStature(performance.getStature());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 ");
		perfor.setTime(sdf.format(performance.getPerformBeginTime()) + "——"
				+ sdf.format(performance.getPerformEndTime()));
		perfor.setTitle(performance.getTitle());
		perfor.setType(performance.getType());
		perfor.setCity(performance.getCity());
		perfor.setSignup(false); // 是否已经报名
		List<SignupArtist> artistList = myPerformanceDao
				.signupArtistOfPerf(performance.getId());
		if (user != null) {
			for (SignupArtist sa : artistList) {
				if (sa.getId().equals(user.getId())) {
					perfor.setSignup(true);
				}
			}
		}
		perfor.setArtistList(artistList);

		return perfor;
	}

	public int performanceSignup(User user, String peformanceId, int price,
			String desc) {
		int result = 0;
		
		if(null==null){
			if (performanceDao.isSelf(peformanceId, user.getId()) > 0){
				result = 3;
			}else{
			
				result = 4;
			}
			
		}else{
			result = 2;
		}
		return result;
	}

	public List<PerforBean> searchByKey(Date date, String key, int page,
			String uid) {
		List<Performance> performanceList = performanceDao.searchByKey(date,
				key, (page - 1) * PAGE_COUNT, PAGE_COUNT);
		List<PerforBean> perList = new ArrayList<PerforBean>();
		PerforBean p = null;
		for (Performance performance : performanceList) {
			p = new PerforBean();
			p.setId(performance.getId());
			p.setAddress(performance.getAddress());
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			System.out.println(performance.getBeginTime());
			String time = dateFormat.format(performance.getBeginTime());

			p.setTime(time);
			p.setPicPath("");
			p.setSalary(performance.getSalary() + "");
			p.setSignupNum(p.getSignupNum());
			p.setSignupState(p.getSignupState());


			p.setTitile(performance.getTitle());
			perList.add(p);
		}
		return perList;
	}
}
