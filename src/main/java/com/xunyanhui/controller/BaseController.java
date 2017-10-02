package com.xunyanhui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xunyanhui.bean.HomeArtist;
import com.xunyanhui.service.ArtistService;


/**
 * 首页视图Controler
 * 
 * @author 柯鑫
 * 
 */
@Controller
@RequestMapping("/base")
@RequiresRoles("user")
public class BaseController {
	@Autowired
	private ArtistService artistService;
	final Logger logger = Logger.getLogger("BaseController");

	/**
	 * 该接口负责首页数据的初始化
	 */

	/**
	 * 首页推荐艺人的翻页功能
	 */
	@ResponseBody
	@RequestMapping("/home/artist/{page}")
	public List<HomeArtist> CommendArtistList(@PathVariable("page") int page,
			HttpServletRequest request) {
		logger.debug("/base/home 获取获取推荐艺人列表\tpage = " + page);
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Date date = (Date) session.getAttribute("homeDate");
		List<HomeArtist> homeArtist = artistService.getreCommendArtistList(
				date, page);
		logger.debug("获取到的主页数据" + homeArtist);
		logger.debug("当前的date为   " + date.getTime());
		System.out.println(date.getTime() + "==============");
		return homeArtist;
	}



}
