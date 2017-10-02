/**
 * 创建日期：2016-12-23下午4:31:56
 * 修改日期：
 * 作者：邢传军
 * 实现我的——演艺活动处理（报名演艺和发布的演艺）
 */
package com.xunyanhui.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.xunyanhui.bean.EvaluationOpus;
import com.xunyanhui.bean.MyArtistOpus;
import com.xunyanhui.bean.MyArtistOpusL;
import com.xunyanhui.bean.MyPerformance;
import com.xunyanhui.bean.MyPerformanceDetail;
import com.xunyanhui.bean.PerfBean;
import com.xunyanhui.bean.Performance;
import com.xunyanhui.bean.SignupArtist;
import com.xunyanhui.bean.SignupArtistOfPerf;
import com.xunyanhui.jms.bean.MessageInfo;
import com.xunyanhui.jms.service.JMSProducer;
import com.xunyanhui.model.ArtistAnnouncement;
import com.xunyanhui.model.EntryRecordN;
import com.xunyanhui.model.User;
import com.xunyanhui.service.MyPerformanceService;
import com.xunyanhui.service.UserService;
import com.xunyanhui.utils.UUidUtil;
import com.xunyanhui.model.ArtPerformanceEntry;



@Controller
@RequestMapping("/")
public class MyPerfromanceController {
	@Autowired
	private MyPerformanceService myPerformanceService;
	//用于jms通讯的实体属性
	/*@Resource
	private JMSProducer jMSProducer;
	@Resource
	private ActiveMQQueue queueDestination;*/
	
	/*
	 * 获取用户报名参加的演艺列表
	 */
	@RequestMapping(value="myArtPerf/getEntryList/{uid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String sendPerformance(@PathVariable("uid") String uid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		int ret = 1;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);		
		if (user != null) {
			List<ArtPerformanceEntry> list = null;
			list =	myPerformanceService.getArtPerformanceEntryList(user.getId());
			if(list==null){
				result.put("state","0");
				result.put("result", null);
			}else{
				result.put("result", list);
			}
			
		} else {
			ret = -1;
		}
		result.put("state", ret);
		return JSONObject.fromObject(result).toString();

	}
	
	/*
	 * 获取用户小样/作品列表
	 */
	@RequestMapping(value="myOpus/getSelfOpusList/{uid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String getSelfOpusList(@PathVariable("uid") String uid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		int ret = 1;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		List<MyArtistOpus> list = null;
		if (user != null) {
			list =	myPerformanceService.getSelfOpusList(user.getId());
			result.put("result", list);
		} else {
			ret = 0;
		}
		result.put("state", ret);
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 获取用户小样/作品详情
	 */
	@RequestMapping(value="myOpus/getSelfOpusById/{uid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String getSelfOpusById(@PathVariable("uid") String uid,@RequestParam(value="objectid") String objectid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		int ret = 1;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		if (user != null) {
			MyArtistOpusL	myArtistOpusL =	myPerformanceService.getSelfOpusById(objectid);
			List<EvaluationOpus> postList = myPerformanceService.getPostOfSelfOpus(objectid);
			myArtistOpusL.setEvaluationOpusList(postList);
			result.put("result", myArtistOpusL);
		} else {
			ret = 0;
		}
		result.put("state", ret);
		return JSONObject.fromObject(result).toString();
	}

	/*
	 * 获取用户发布的全部演艺列表
	 */
	@RequestMapping(value="myArtPerf/getSendList/{uid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String getSendList(@PathVariable("uid") String uid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		int ret = 1;
		List<MyPerformance> list = null;
		if (user != null) {
			list =	myPerformanceService.getSendList(user.getId());
			result.put("result", list);
		} else {
			ret = 0;
		}
		result.put("state", ret);
		return JSONObject.fromObject(result).toString();

	}
	
	/*
	 * 获取用户发布的某一演艺详情
	 */
	@RequestMapping(value="myArtPerf/getDetail/{uid}/{performanceid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String getDetail(@PathVariable("uid") String uid,@PathVariable("performanceid") String performanceid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		MyPerformanceDetail perf = null;
		if (user != null) {
			perf =	myPerformanceService.getPerformanceDetailsOfUser(user.getId(), performanceid);
			if(perf.getGender().equals('1')){
				perf.setGender("男");
			}
			else
			{
				if(perf.getGender().equals('2'))
					perf.setGender("女");
				else
					perf.setGender("男女不限");
				
			}
			if(perf!=null){
				List<SignupArtistOfPerf> signupArtistList = myPerformanceService.signupArtistOfPerfByUser(performanceid, user.getId());
				if(signupArtistList!=null){
					perf.setArtistList(signupArtistList);
				}
				result.put("result", perf);
				result.put("msg", "成功");
				result.put("code", "1");
			}else{
				result.put("msg", "无数据满足条件");
				result.put("code", "0");
			}
			
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 对某一艺人报名的演艺活动进行处理——成交
	 * 首先检查演艺活动是否为登录用户发布的
	 * 然后才进行成交活动
	 */
	@RequestMapping(value="myArtPerf/inOrder/{uid}/{performanceid}/{artistid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String inOrder(@PathVariable("uid") String uid,@PathVariable("performanceid") String performanceid,@PathVariable("artistid") String artistid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		if (user != null) {
			int isEnabel = myPerformanceService.checkPerformanceOfUser(user.getId(), performanceid);
			if(isEnabel==1){
				EntryRecordN entryRecordN= myPerformanceService.getEntryRecord(artistid, performanceid);
				if(entryRecordN.getState()==0){
					isEnabel = myPerformanceService.updateEntryState(artistid,performanceid, 1);
					if (isEnabel == 1) {
						result.put("msg", "成功");
						result.put("code", "1");
					} else {
						result.put("msg", "修改失败");
						result.put("code", "0");
					}
				}
				else{
					result.put("msg", "状态异常");
					result.put("code", "-3");
				}
				
			}else{
				result.put("msg", "演艺活动非本人发布");
				result.put("code", "-2");
			}
			
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 对某一艺人报名的演艺活动进行处理——结算
	 * 首先检查演艺活动是否为登录用户发布的
	 * 然后才进行成交活动
	 */
	@RequestMapping(value="myArtPerf/inCount/{uid}/{performanceid}/{artistid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String inCount(@PathVariable("uid") String uid,@PathVariable("performanceid") String performanceid,@PathVariable("artistid") String artistid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		if (user != null) {
			int isEnabel = myPerformanceService.checkPerformanceOfUser(user.getId(), performanceid);
			if(isEnabel==1){
				EntryRecordN entryRecordN= myPerformanceService.getEntryRecord(artistid, performanceid);
				if(entryRecordN.getState()==1){
					isEnabel = myPerformanceService.updateEntryState(artistid,performanceid, 2);
					if (isEnabel == 1) {
						result.put("msg", "成功");
						result.put("code", "1");
					} else {
						result.put("msg", "修改失败");
						result.put("code", "0");
					}
				}
				else{
					result.put("msg", "状态异常");
					result.put("code", "-3");
				}
				
			}else{
				result.put("msg", "演艺活动非本人发布");
				result.put("code", "-2");
			}
			
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 对某一艺人报名的演艺活动进行处理——支付
	 * 首先检查演艺活动是否为登录用户发布的
	 * 然后才进行成交活动
	 */
	@RequestMapping(value="myArtPerf/inPay/{uid}/{performanceid}/{artistid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String inPay(@PathVariable("uid") String uid,@PathVariable("performanceid") String performanceid,@PathVariable("artistid") String artistid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		if (user != null) {
			int isEnabel = myPerformanceService.checkPerformanceOfUser(user.getId(), performanceid);
			if(isEnabel==1){
				EntryRecordN entryRecordN= myPerformanceService.getEntryRecord(artistid, performanceid);
				if(entryRecordN.getState()==2){
					isEnabel = myPerformanceService.updateEntryState(artistid,performanceid, 3);
					if (isEnabel == 1) {
						result.put("msg", "成功");
						result.put("code", "1");
					} else {
						result.put("msg", "修改失败");
						result.put("code", "0");
					}
				}
				else{
					result.put("msg", "状态异常");
					result.put("code", "-3");
				}
				
			}else{
				result.put("msg", "演艺活动非本人发布");
				result.put("code", "-2");
			}
			
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 获取用户发布的可报名的演艺列表
	 */
	@RequestMapping(value="myArtPerf/getEnableList/{uid}/{artistId}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String getEnableList(@PathVariable("uid") String uid,@PathVariable("artistId") String artistId,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		int ret = 1;
		List<MyPerformance> list = null;
		List<ArtistAnnouncement> list_ann=null;
		//if (user != null) {
			
			list =	myPerformanceService.getEnableList(uid);
			list_ann = myPerformanceService.getAnnounceOfArtist(artistId);
			result.put("performance", list);
			result.put("announce", list_ann);
		//} else {
		//	ret = 0;
		//}
		result.put("state", ret);
		return JSONObject.fromObject(result).toString();

	}
	/*
	 * 关闭演艺活动报名
	 */
	@RequestMapping(value="myArtPerf/closed/{performanceId}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String closePerformance(@PathVariable("performanceId") String performanceId,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		int ret = 1;
		if (user != null) {
		    MyPerformanceDetail perf = myPerformanceService.getPerformanceDetailsOfUser(user.getId(), performanceId);
			if(perf!=null){
				//修改演艺活动状态
				int signupState = perf.getSignupState();
				if(signupState==1){
					int ret1 = myPerformanceService.updateSignUpState(user.getId(), performanceId, 0);
					if(ret1==1){
						result.put("msg", "演艺活动更新为不可报名状态！");
						result.put("code", "1");
					}else{
						result.put("msg", "演艺活动更新不可报名状态失败！");
						result.put("code", "0");
					}
				}
				else{
					result.put("msg", "演艺活动当前已经为不可报名状态！");
					result.put("code", "-3");
				}
			}else{
				result.put("msg", "演艺活动不存在！");
				result.put("code", "-2");
			}
		} else {
			result.put("msg", "用户尚未登录！");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();

	}
	
	/*
	 * 修改演艺报名的报价，前提是报名时未成交状态 
	 * @param artistid				艺人id
	 * @param performanceId			演艺活动状态id
	 * @param price					修改后的演艺活动报价
	 * @param description			修改后的演艺活动报价说明
	 * @return
	 */
	@RequestMapping(value="myArtPerf/updatePrice/{performanceId}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String updatePrice(@PathVariable("performanceId") String performanceId,
			@RequestParam(value="price") int price,
			@RequestParam(value="description") String description,
			@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		System.out.println(price);
		System.out.println(description);
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		int ret = 1;
		if (user != null) {
		    int isChangePrice = myPerformanceService.updatePrice(user.getId(), performanceId, price, description);
			System.out.println(isChangePrice);
		    if(isChangePrice!=0){
				result.put("msg", "修改报价成功！");
				result.put("code", "1");
				System.out.println(price);
				System.out.println(description);
			}else{
				result.put("msg", "修改报价失败！");
				result.put("code", "0");
				System.out.println(price);
				System.out.println(description);
			}

		} else {
			result.put("msg", "用户尚未登录！");
			result.put("code", "-1");
			System.out.println(price);
			System.out.println(description);
		}
		return JSONObject.fromObject(result).toString();

	}
	
	/*
	 * 某一艺人在报名的某一演艺活动为确认成交前可以取消报名
	 * 首先检查演艺活动是否为登录用户发布的
	 * 然后才进行成交活动
	 */
	@RequestMapping(value="myArtPerf/cancel/{performanceid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String cancel(@PathVariable("performanceid") String performanceid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		Map<String,Object> result = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		if (user != null) {
			EntryRecordN entryRecordN= myPerformanceService.getEntryRecord(user.getId(), performanceid);
			if(entryRecordN!=null){
				if(entryRecordN.getState()==0){
					int isEnabel = myPerformanceService.updateEntryState(user.getId(),performanceid, 4);
					if (isEnabel == 1) {
						result.put("msg", "取消报名成功");
						result.put("code", "1");
					} else {
						result.put("msg", "取消报名失败");
						result.put("code", "0");
					}
				}
				else{
					result.put("msg", "当前为不可取消状态");
					result.put("code", "-3");
				}	
			}else{
				result.put("msg", "演艺活动非本人报名");
				result.put("code", "-2");
			}
			
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	
	/*
	 * 实现用户对某一艺人的关注或取消关注，需要用户登录后进行操作
	 */
	@RequestMapping(value="myArtist/attention/{uid}/{artistId}/{state}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String setAttention(@PathVariable("uid") String uid,@PathVariable("artistId") String artistId,@PathVariable("state") int state,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String,String> retMap = new HashMap<String,String>();
		int ret = 0;
		if (user != null) {
			int attention_state = myPerformanceService.getAttention(user.getId(), artistId);
			if(attention_state==0){
				String uaid = UUidUtil.getUUid();
				ret = myPerformanceService.newAttention(user.getId(), artistId,uaid);
			}else{
				ret = myPerformanceService.updateAttention(user.getId(), artistId, state);
			}
			if(ret==0){
				retMap.put("msg", "失败");
				retMap.put("code", "0");
			}
			else{
				if(state==1){
					//发送关注类型消息到jms系统
					MessageInfo msg = new MessageInfo();
					msg.setType(7);
					msg.setSid(user.getId());
					msg.setDid(artistId);//被关注艺人id
					msg.setValue(user.getId());//关注用户id
					//jMSProducer.sendMessage(queueDestination, JSONObject.fromObject(msg).toString());
					System.out.println("你关注了了");
				}
				retMap.put("msg", "成功");
				retMap.put("code", "1");
			}
		} else {
			retMap.put("msg", "未登录");
			retMap.put("code", "-1");
		}
		return JSONObject.fromObject(retMap).toString();
	}
	/*
	 * 获取艺人通告列表
	 */
	@RequestMapping(value="mySelf/announce/{uid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String getAnnounceOfArtist(@PathVariable("uid") String uid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody byte[] bytes) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String,Object> retMap = new HashMap<String,Object>();
		if (user != null) {
			List<ArtistAnnouncement> listA = myPerformanceService.getAnnounceOfArtist(user.getId());
			if(listA==null){
				retMap.put("msg", "失败");
				retMap.put("code", "0");
			}
			else{
				retMap.put("msg", "成功");
				retMap.put("code", "1");
				retMap.put("result", listA);
			}
		} else {
			retMap.put("msg", "未登录");
			retMap.put("code", "-1");
		}
		return JSONObject.fromObject(retMap).toString();
	}
	/*
	 * 艺人新建通告
	 */
	@RequestMapping(value="mySelf/newAnnounce/{uid}", method = RequestMethod.POST,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String newAnnounce(@PathVariable("uid") String uid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request, @RequestBody ArtistAnnouncement artistAnnouncement) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String,Object> retMap = new HashMap<String,Object>();
		if (user != null) {
			try{
				if (artistAnnouncement != null) {
					artistAnnouncement.setUid(user.getId());
					artistAnnouncement.setAid(UUidUtil.getUUid());
					int i_ret = myPerformanceService.newAnnounce(artistAnnouncement);
					if (i_ret == 1) {
						retMap.put("msg", "成功");
						retMap.put("code", "1");
					} else {
						retMap.put("msg", "失败");
						retMap.put("code", "0");
					}
				} else {
					retMap.put("msg", "数据异常");
					retMap.put("code", "-1");
				}
			}
			catch(Exception e){
				retMap.put("msg", "服务异常");
				retMap.put("code", "-3");
				return JSONObject.fromObject(retMap).toString();
			}
		} else {
			retMap.put("msg", "未登录");
			retMap.put("code", "-1");
		}
		return JSONObject.fromObject(retMap).toString();
	}
	/*
	 * 艺人新建通告
	 */
	@RequestMapping(value="mySelf/cancelAnnounce/{uid}/{aid}", method = RequestMethod.POST,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String cancelAnnounce(@PathVariable("uid") String uid,@PathVariable("aid") String aid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String,Object> retMap = new HashMap<String,Object>();
		if (user != null) {
			try{
				int i_ret = myPerformanceService.cancelAnnounce(user.getId(), aid);
				if (i_ret == 1) {
					retMap.put("msg", "成功");
					retMap.put("code", "1");
				} else {
					retMap.put("msg", "失败");
					retMap.put("code", "0");
				}
			}
			catch(Exception e){
				retMap.put("msg", "服务异常");
				retMap.put("code", "-3");
				return JSONObject.fromObject(retMap).toString();
			}
		} else {
			retMap.put("msg", "未登录");
			retMap.put("code", "-1");
		}
		return JSONObject.fromObject(retMap).toString();
	}
	/*
	 * 测试读取演艺报名的艺人的简要信息
	 */
	@RequestMapping(value="mySelf/test1/{uid}/{performanceid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String test1(@PathVariable("uid") String uid,@PathVariable("performanceid") String performanceid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String,Object> retMap = new HashMap<String,Object>();
		List<SignupArtist> list = myPerformanceService.signupArtistOfPerf(performanceid);
		return JSONArray.fromObject(list).toString();
	}
	/*
	 * 测试读取演艺报名的艺人的简要信息
	 */
	@RequestMapping(value="mySelf/test2/{uid}/{performanceid}", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String test2(@PathVariable("uid") String uid,@PathVariable("performanceid") String performanceid,@RequestParam(value="token",required=false) String token,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String,Object> retMap = new HashMap<String,Object>();
		List<SignupArtistOfPerf> list = myPerformanceService.signupArtistOfPerfByUser(performanceid, uid);
		return JSONArray.fromObject(list).toString();
	}
	
}
