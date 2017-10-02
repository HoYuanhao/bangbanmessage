/**
 * 创建日期：2017-1-11下午4:06:09
 * 修改日期：
 * 作者：邢传军
 * 处理城市列表
 */
package com.xunyanhui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xunyanhui.bean.AccountBalance;
import com.xunyanhui.bean.PerforBean;
import com.xunyanhui.model.City;
import com.xunyanhui.model.User;
import com.xunyanhui.service.AccountService;
import com.xunyanhui.service.CityService;
import com.xunyanhui.service.UserService;

@Controller
@RequestMapping("/")
public class CityController {
	/** 日志实例 */
	private static final Logger logger = Logger.getLogger(CityController.class);
	@Resource
	CityService cityService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "city/getCityList", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String getCityList(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Map<String, Object> result = new HashMap<String, Object>();
		List<City> cityList = cityService.getCityList();
		if (cityList != null && cityList.size() > 0) {
			List<City> cityListTmp = null;
			TreeMap<String, Object> cityMap = new TreeMap<String, Object>();
			for (int i = 0; i < cityList.size(); i++) {
				City city = new City();
				city = cityList.get(i);
				String firstChar = city.getPinyin().substring(0, 1);
				cityListTmp = (List<City>) cityMap.get(firstChar);
				if (cityListTmp == null) {
					cityListTmp = new ArrayList<City>();
				}
				cityListTmp.add(city);
				cityMap.put(firstChar, cityListTmp);
			}
			result.put("cityList", cityMap);
			result.put("msg", "获取城市目录成功");
			result.put("code", "1");
		}
		else{
			result.put("msg", "无城市目录！");
			result.put("code", "0");
		}
		
		return JSONObject.fromObject(result).toString();
	}

}
