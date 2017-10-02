package com.xunyanhui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xunyanhui.bean.Base64String;
import com.xunyanhui.bean.FileSaveRet;
import com.xunyanhui.bean.PerfBean;
import com.xunyanhui.bean.PerfBeanImg;
import com.xunyanhui.bean.PerforBean;
import com.xunyanhui.bean.Performance;
import com.xunyanhui.model.ArtistAnnouncement;
import com.xunyanhui.model.User;
import com.xunyanhui.service.MyPerformanceService;
import com.xunyanhui.service.PerformanceService;
import com.xunyanhui.service.UserService;
import com.xunyanhui.utils.FileUtil;
import com.xunyanhui.utils.UUidUtil;

@Controller
@RequestMapping("/perf")
public class PerformanceControler {
	
	/** 日志实例 */
	private static final Logger logger = Logger.getLogger("PerformanceControler");
	//获取配置中的演艺活动(列表)文件上传路径和网络访问位置
	private @Value("#{test['REPOSITORY_PATH_PERFORMANCE']}")  String head_upload_path;
	private @Value("#{test['IMAGE_BASE_URL_PERFORMANCE']}")  String head_get_path;
	//获取配置中的演艺活动(封面)文件上传路径和网络访问位置
	private @Value("#{test['REPOSITORY_PATH_PERFORMANCE_HOME']}")  String head_home_upload_path;
	private @Value("#{test['IMAGE_BASE_URL_PERFORMANCE_HOME']}")  String head_home_get_path;
		
	//获取配置中的文件的临时中转路径
	private @Value("#{test['REPOSITORY_PATH_TEMP']}")  String file_tmp;
	@Autowired
	private PerformanceService performanceService;
	@Autowired
	private MyPerformanceService myPerformanceService;

	@RequestMapping("list.json")
	@ResponseBody
	public List<PerforBean> getPerformanceFirstPage(
			@RequestParam(value = "key", required = false) String key,
			@RequestParam(value = "order", required = false, defaultValue = "0") int order,
			@RequestParam(value = "type", required = false) String type,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		Search s = new Search();
		s.date = new Date();
		s.order = order;
		s.type = type;
		s.key = key;
		if(session==null){
			System.out.println("session is null");
		}
		session.setAttribute("perfSearch", s);
		logger.error("performance  list first");
		String uid = null;
		Subject subject = SecurityUtils.getSubject();
		boolean isLogin = subject.hasRole("user");
		User user=null;
		if (isLogin) {
			user = (User) session.getAttribute("user");
			uid = user.getId();
		}
		List<PerforBean> performanceList = null;
		if (key != null && !key.equals("")) {
			System.out.println("First key"+s.key);
			performanceList = performanceService.searchByKey(s.date, "%"+s.key+"%", 1,
					uid);
		} else {

			performanceList = performanceService.getPerformanceList(s.date,
					order, type, 1, uid);
		}
		return performanceList;
	}

	@RequestMapping("list/{page}.json")
	@ResponseBody
	public List<PerforBean> getPerformancePage(
			@PathVariable(value = "page") int page, HttpServletRequest request) {
		HttpSession session = request.getSession();

		Search s = (Search) session.getAttribute("perfSearch");
		if (s == null) {
			return null;
		}
		String uid = null;
		Subject subject = SecurityUtils.getSubject();
		boolean isLogin = subject.hasRole("user");
		if (isLogin) {
			User user = (User) session.getAttribute("user");
			uid = user.getId();
		}
		logger.error("performance :page"+page);
		List<PerforBean> performanceList = null;
		if (s.key != null && !s.key.equals("")) {
			System.out.println("page key"+s.key);
			performanceList = performanceService.searchByKey(s.date, "%"+s.key+"%",
					page, uid);
		} else {
			performanceList = performanceService.getPerformanceList(s.date,
					s.order, s.type, page, uid);
		}
		//System.out.println(JSONObject.fromObject(performanceList).toString());
		return performanceList;
	}

	@RequestMapping(value="/sendPerf", method = RequestMethod.POST,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String sendPerformance(
			HttpServletRequest request, @RequestBody PerfBeanImg perfBeanimg) {
		HttpSession session = request.getSession();
		PerfBean perfBean1 = perfBeanimg.getPerfBean(); 
		String base64 = perfBeanimg.getPath();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		String result = null;
		if (user != null) {
			List<String> path = new ArrayList<String>();
			path.add(this.getHead_home_upload_path());
			path.add(this.getHead_upload_path());
			String pid = UUidUtil.getUUid();
			FileSaveRet fileRet = FileUtil.saveAsBase64(base64, path, pid);
			perfBean1.setUid(user.getId());
			perfBean1.setReleaseTime(new Date());
			if(fileRet.getCode()==1){
				if (performanceService.addPerformance(perfBean1,pid,fileRet.getPic_extra())) {
					result = "{\"msg\":\"成功\",\"code\":\"1\"}";
				} else {
					result = "{\"msg\":\"失败\",\"code\":\"0\"}";
				}
			}
			else{
				result = "{\"msg\":\""+fileRet.getMsg()+"\",\"code\":\""+fileRet.getCode()+"\"}";
			}

		} else {
			result = "{\"msg\":\"登录失效\",\"code\":\"-1\"}";
		}
		System.out.println(result);
		return result;

	}

	@RequestMapping("getPerf/{perforId}")
	@ResponseBody
	public Performance getPerformance(
			@PathVariable("perforId") String perforId,
			HttpServletRequest request) {

		Performance performance = performanceService.getPerformance(perforId,
				request);

		return performance;
	}

	/**
	 * 演艺报名
	 * peformanceId			报名的演艺Id
	 * price				报名价格
	 * desc					报名说明
	 * @return
	 */
	@RequestMapping(value="/signup", method = RequestMethod.GET,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public String performanceSignup(
			@RequestParam("peformanceId") String peformanceId,
			@RequestParam("price") String price,
			@RequestParam("desc") String desc,
			HttpServletRequest request) {
		System.out.println("performId:"+peformanceId+",price:"+price+",desc:"+desc);
		Map<String,Object> retMap = new HashMap<String,Object>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		if(user!=null){
			int is_Artist = myPerformanceService.isArtist(user.getId());
			//System.out.println("is_Artist"+is_Artist+",cid:"+user.getId());
			if(is_Artist==1){
				Integer i_price;
				try{
					i_price = Integer.valueOf(price);
				}
				catch(NumberFormatException e){
					retMap.put("msg", "数据异常");
					retMap.put("code", "-2");
					return JSONObject.fromObject(retMap).toString();
				}
				
				int result = performanceService
						.performanceSignup(user, peformanceId,i_price,desc);
				switch(result){
				case 2:
					retMap.put("msg", "不允许重复报名");
					retMap.put("code", "2");
					break;
				case 3:
					retMap.put("msg", "不允许自己报名");
					retMap.put("code", "3");
					break;
				case 1:
					retMap.put("msg", "报名成功");
					retMap.put("code", "1");
					break;
				default:
					retMap.put("msg", "其他未知错误");
					retMap.put("code", "4");
					break;	
				}
			}
			else{
				retMap.put("msg", "当前用户尚未成为艺人");
				retMap.put("code", "5");
			}
			
		}
		else{
			retMap.put("msg", "未登录");
			retMap.put("code", "-1");
		}
		
		return JSONObject.fromObject(retMap).toString();
	}

	public String getHead_upload_path() {
		return head_upload_path;
	}

	public void setHead_upload_path(String head_upload_path) {
		this.head_upload_path = head_upload_path;
	}

	public String getHead_get_path() {
		return head_get_path;
	}

	public void setHead_get_path(String head_get_path) {
		this.head_get_path = head_get_path;
	}

	public String getHead_home_upload_path() {
		return head_home_upload_path;
	}

	public void setHead_home_upload_path(String head_home_upload_path) {
		this.head_home_upload_path = head_home_upload_path;
	}

	public String getHead_home_get_path() {
		return head_home_get_path;
	}

	public void setHead_home_get_path(String head_home_get_path) {
		this.head_home_get_path = head_home_get_path;
	}

	public String getFile_tmp() {
		return file_tmp;
	}

	public void setFile_tmp(String file_tmp) {
		this.file_tmp = file_tmp;
	}

	private class Search {
		String key;
		int order;
		String type;
		Date date;
	}
}
