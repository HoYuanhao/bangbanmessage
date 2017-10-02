package com.xunyanhui.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xunyanhui.bean.UserInfoSimple;
import com.xunyanhui.exception.DaoException;
import com.xunyanhui.model.Account;
import com.xunyanhui.model.User;
import com.xunyanhui.service.AccountService;
import com.xunyanhui.service.MyInfoService;
import com.xunyanhui.service.SmsSendService;
import com.xunyanhui.service.UserService;
import com.xunyanhui.utils.RegUtil;
import com.xunyanhui.utils.UUidUtil;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	@Autowired
	private SmsSendService smsSendService;
	@Autowired
	private UserService userService;
	@Autowired
	private MyInfoService myInfoService;
	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String test1(HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		return "{\"msg\":\"error\"}";
	}

	/*
	 * 登录
	 */
	@RequestMapping(value = "/login", produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String login(HttpServletRequest request,
			@RequestParam("userName") String userName,
			@RequestParam("password") String password) {
		String error = null;
		Subject subject = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(userName,
				password);
		try {
			subject.login(token);
		} catch (UnknownAccountException e) {
			error = "用户名错误";
		} catch (IncorrectCredentialsException e) {
			error = "密码错误";
		} catch (DaoException e) {
			error = "数据库操作错误";
		} catch (AuthenticationException e) {

			// 其他错误，比如锁定，如果想单独处理请单独catch处理
			error = "其他错误：" + e.getMessage();
		}
		System.out.println(error);
		System.out.println("是否有 user 权限 :" + subject.hasRole("user"));
		if (error != null) {
			return "{\"msg\":\"" + error + "\",\"code\":\"0\"}";
		} else {
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute(UserService.LOGIN_USER);
			UserInfoSimple userInfoSimple = myInfoService
					.getSimpleUserInfo(user.getId());
			JSONObject.fromObject(userInfoSimple).toString();
			return "{\"msg\":\"登录成功\",\"code\":\"1\",\"userInfo\":"
					+ JSONObject.fromObject(userInfoSimple).toString() + "}";
		}
	}

	/*
	 * 登出
	 */
	@RequestMapping(value = "/logout", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String logout() {
		Subject subject = SecurityUtils.getSubject();
		subject.logout();

		return "haha";
	}

	@RequestMapping(value = "/sid", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public void getSid(HttpServletRequest request) {
		System.out.println("sid");
		request.getSession();
	}

	/*
	 * 注册接口
	 */
/*
 * ----------------------------------
 * | coce  |  1  | 注册成功			|
 * ----------------------------------
 * | coce  | -1  | 用户名不符合规则	|
 * ----------------------------------
 * | coce  | -2  | 手机号不符合规则	|
 * ----------------------------------
 * | coce  | -3  | 密码不符合规则		|
 * ----------------------------------
 * | coce  | -4  | 手机号不符合规则	|
 * ----------------------------------
 * | coce  | -5  | 用户名不符合规则	|
 * ----------------------------------
 * | coce  | -6  | 手机验证码输入错误	|
 * ----------------------------------
 */
	@RequestMapping(value = "/regist", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String regist(@RequestParam("mobile") String mobile,
			@RequestParam("code") String code,
			@RequestParam("password") String password,
			@RequestParam("userName") String userName,
			@RequestParam("cid") String cid) {
		System.out.println("注册  mobile:" + mobile + "userName:" + userName
				+ "dsfd s  " + code);
		String result = null;

		if (!RegUtil.regUserName(userName)) {
			result = "{\"code\":-1}";
		} else if (!RegUtil.regMobile(mobile)) {
			result = "{\"code\":-2}";
		} else if (!RegUtil.regPasswd(password)) {
			result = "{\"code\":-3}";
		} else if (userService.hasPhoneNum(mobile)) {
			result = "{\"code\":-4}";
		} else if (userService.hasUserName(userName)) {
			result = "{\"code\":-5}";
		} else if (!smsSendService.verifyCode(mobile, code)) {
			result = "{\"code\":-6}";
		} else {
			User user = new User();
			user.setUserName(userName);
			user.setPassword(password);
			user.setMobile(mobile);
			user.setRegisterTime(new Date());
			user.setCid(cid);
			user.setId(UUidUtil.getUUid());
			System.out.println("注册中");
			if (userService.regist(user)) {
				Account account = new Account();
				account.setPassword(user.getPassword());
				account.setUserName(user.getUserName());
				account.setMobile(user.getMobile());
				account.setUid(user.getId());
				account.setAccountId(UUidUtil.getUUid());
				accountService.newAccount(account);
				result = "{\"code\":1}";
			}

		}

		return result;

		/*
		 * if (!userService.hasPhoneNum(mobile) &&
		 * !userService.hasUserName(userName) )
		 */
	}

	/**
	 * 获取验证码
	 * 
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/verifyMobile/{mobile}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String verifyMobile(@PathVariable("mobile") String mobile) {
		String result = null;
		System.out.println("verifyMobile" + mobile);
		if (userService.hasPhoneNum(mobile)) {
			result = "{\"code\":0}";
		} else {
			result = "{\"code\":1}";
		}

		return result;
	}

	@RequestMapping(value = "/verifyUserName/{userName}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String verifyUserName(@PathVariable("userName") String userName) {
		String result = null;
		System.out.println("verifyUserName" + userName);
		if (userService.hasUserName(userName)) {
			result = "{\"code\":0}";
		} else {
			result = "{\"code\":1}";
		}

		return result;
	}

	@RequestMapping(value = "/sendSMS/{mobile}", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String sendSMS(@PathVariable("mobile") String mobile,
			HttpServletRequest request) {
		System.out.println("sendSMS" + mobile);
		String code = smsSendService.getCode(mobile);

		if (code != null) {
			HttpSession session = request.getSession();
			session.setAttribute("code", code);
			smsSendService.send(mobile, code);
			return "{\"code\":1}";
		} else {
			return "{\"code\":0}";
		}
	}
}
