/**
 * 创建日期：2016-12-20下午2:14:27
 * 修改日期：
 * 作者：邢传军
 * 目的：实现小样或作品的操作类
 */
package com.xunyanhui.controller;

import java.io.UnsupportedEncodingException;

import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;

import com.xunyanhui.bean.UserSubscribeV;
import com.xunyanhui.bean.UserSubscribeVS;

import com.xunyanhui.service.UserSubscribeService;

@Controller
@RequestMapping(value = "/userSub")
public class UserSubscribeController {
	/** 日志实例 */
	private static final Logger logger = Logger
			.getLogger("ArtistOpusController");

	@Resource
	UserSubscribeService userSubscribeService;

	/*
	 * 查询指定用户的订阅
	 */
	@RequestMapping(value = "/getByUser/{uid}/{token}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String listByUid(@PathVariable("token") String token,
			@PathVariable("uid") String uid)
			throws UnsupportedEncodingException {

		List<UserSubscribeV> userSubscribeVList = userSubscribeService
				.selectUserSubscribeByUid(uid);
		return JSONArray.fromObject(userSubscribeVList).toString();
	}

	/*
	 * 维护指定用户的指定演艺活动订阅情况
	 */
	@RequestMapping(value = "/updateSubByUser/{uid}/{token}/{pid}/{state}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String updateUserSubscribeByUid(@PathVariable("token") String token,
			@PathVariable("uid") String uid, @PathVariable("pid") int pid,
			@PathVariable("state") int substate)
			throws UnsupportedEncodingException {

		int ret = userSubscribeService.updateUserSubscribeByUid(uid, pid,
				substate);
		return "{\"result\":" + ret + "}";
	}

	/*
	 * 批量维护指定用户的订阅状态
	 */
	@RequestMapping(value = "/updateSubAllByUser/{uid}/{token}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String updateUserAllSubscribeByUid(@PathVariable("token") String token,
			@PathVariable("uid") String uid,
			@RequestParam(value = "result", required = false) String result)
			throws UnsupportedEncodingException {
		int ret = 0;
		try {
			JSONArray json = JSONArray.fromObject(result);
			@SuppressWarnings("unchecked")
			List<UserSubscribeV> persons = (List<UserSubscribeV>) JSONArray
					.toCollection(json, UserSubscribeV.class);

			for (int i = 0; i < persons.size(); i++) {
				ret = ret
						+ userSubscribeService.updateUserSubscribeByUid(persons
								.get(i).getUid(), persons.get(i).getPid(),
								persons.get(i).getSubState());
			}
		} catch (Exception e) {
			ret = 0;
		}
		return "{\"result\":" + ret + "}";
	}

	/*
	 * 查询订阅指定演艺的订阅用户列表
	 */
	@RequestMapping(value = "/selectSubscribeByPid/{pid}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String selectSubscribeByPid(@PathVariable("pid") int pid)
			throws UnsupportedEncodingException {
		List<UserSubscribeVS> list = userSubscribeService
				.selectSubscribeByPid(pid);
		return JSONArray.fromObject(list).toString();
	}

}
