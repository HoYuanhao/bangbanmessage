/**
 * 创建日期：2017-1-11下午4:06:09
 * 修改日期：
 * 作者：邢传军
 */
package com.xunyanhui.controller;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;


import org.apache.commons.codec.digest.DigestUtils;

import org.apache.log4j.Logger;
import cn.beecloud.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



import com.xunyanhui.bean.Bill;


import com.xunyanhui.model.TransactionRecord;
import com.xunyanhui.model.User;
import com.xunyanhui.service.AccountService;
import com.xunyanhui.service.MyInfoService;
import com.xunyanhui.service.SmsSendService;
import com.xunyanhui.service.UserService;

@Controller
@RequestMapping("/")
public class AccountController {
	/** 日志实例 */
	private static final Logger logger = Logger
			.getLogger(AccountController.class);
	/*
	 * beecloud的相关信息
	 */
	private static String app_id = "7d722e6a-2dad-437d-8bf7-3c76b1e4ef70";
	private static String test_secret = "d28dc2e5-a642-4723-8d22-43697bf21775";
	private static int PAGE_LENGTH = 5;
	@Resource
	AccountService accountService;
	@Resource
	MyInfoService myInfoService;
	@Resource
	SmsSendService smsSendService;
	
	
	/*
	 * 用于beecloud的充值回调接收处理
	 * 
	 */

	@RequestMapping(value = "account/checkBeeCloud", method = RequestMethod.POST,produces ="application/json;charset=UTF-8")
	@ResponseBody
	public	String checkBeeCloud(HttpServletRequest request,@RequestBody String s) {
		/*
		 * 接收beecloud消息后进行订单验证
		 * 根据验证结果向用户推送通知消息或短信
		 */
		 /* *
		     功能：BeeCloud服务器异步通知页面
		     //***********页面功能说明***********
		     创建该页面文件时，请留心该页面文件中无任何HTML代码及空格。
		     该页面不能在本机电脑测试，请到服务器上做测试。请确保外部可以访问该页面。
		     如果没有收到该页面返回的 success 信息，BeeCloud会在36小时内按一定的时间策略重发通知
		     //********************************
		     * */
		String ret_String;
	    String appID="7d722e6a-2dad-437d-8bf7-3c76b1e4ef70";
	    String testSecret="d28dc2e5-a642-4723-8d22-43697bf21775";
	    String appSecret="b43bfbe4-86ee-4fed-977b-6089aa4d39d0";
	    String masterSecret="150e9092-e6d3-4424-a306-f20aa9338b25";
	    BeeCloud.registerApp(appID,testSecret, appSecret, masterSecret);
	    StringBuffer json = new StringBuffer();
	    json.append(s);
	    JSONObject jsonObj = JSONObject.fromObject(json.toString());
	    String signature = jsonObj.getString("signature");
	    String transactionId=jsonObj.getString("transaction_id");
	    String transactionType=jsonObj.getString("transaction_type");
	    String channelType=jsonObj.getString("channel_type");
	    String transactionFee=jsonObj.getString("transaction_fee");
	    StringBuffer toSign = new StringBuffer();
	    toSign.append(BCCache.getAppID()).append(transactionId)
	            .append(transactionType).append(channelType)
	            .append(transactionFee);
	   boolean status = verifySign(toSign.toString(),masterSecret,signature);
	    if (status) { //验证成功
	    	ret_String = "success"; //请不要修改或删除
	    	TransactionRecord tr = accountService.getTransactionById(transactionId);
	    	Integer totle_fee = tr.getMoney();
	    	Integer i_transactionFee = Integer.valueOf(transactionFee);
	    	String mobile,notifyContent;
	    	if(totle_fee==i_transactionFee){
	    		//渠道返回金额与用户实际交易金额一致将交易状态进行修改
	    		Integer t_ret = accountService.updateTRState(transactionId, 8);
	    		Integer b_ret = accountService.updateBanlance(tr.getPaymentId(), i_transactionFee);
	    		if(t_ret==1&&b_ret==1){
	    			mobile = accountService.getMobile(tr.getPaymentId());
	    			float yuan = (float)i_transactionFee/100;
		    		DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
		    		String s_yuan = df.format(yuan);//得到返回金额，以元为单位
		    		notifyContent = "充值成功，金额："+s_yuan+"元";
		    		smsSendService.sendNotify("13199455545", notifyContent);
	    		}else{
	    			ret_String = "fail";
	    		}
	    		
	    	}else{
	    		mobile = accountService.getMobile(tr.getPaymentId());
	    		notifyContent = "交易异常账户："+tr.getPaymentId()+",订单号为："+transactionId;
	    		smsSendService.sendNotify("13199455545", notifyContent);
	    		logger.error("交易异常账户："+tr.getPaymentId()+",订单号为："+transactionId);
	    	}
	        // 此处需要验证购买的产品与订单金额是否匹配:
	        // 验证购买的产品与订单金额是否匹配的目的在于防止黑客反编译了iOS或者Android app的代码，
	        // 将本来比如100元的订单金额改成了1分钱，开发者应该识别这种情况，避免误以为用户已经足额支付。
	        // Webhook传入的消息里面应该以某种形式包含此次购买的商品信息，比如title或者optional里面的某个参数说明此次购买的产品是一部iPhone手机，
	        // 开发者需要在客户服务端去查询自己内部的数据库看看iPhone的金额是否与该Webhook的订单金额一致，仅有一致的情况下，才继续走正常的业务逻辑。
	        // 如果发现不一致的情况，排除程序bug外，需要去查明原因，防止不法分子对你的app进行二次打包，对你的客户的利益构成潜在威胁。
	        // 如果发现这样的情况，请及时与我们联系，我们会与客户一起与这些不法分子做斗争。而且即使有这样极端的情况发生，
	        // 只要按照前述要求做了购买的产品与订单金额的匹配性验证，在你的后端服务器不被入侵的前提下，你就不会有任何经济损失。
	        // 处理业务逻辑
	    } else { //验证失败
	    	ret_String = "fail";
	    }
	    
	    return ret_String;
	    
	}
	
	private  boolean verifySign(String text,String masterKey,String signature) {
        boolean isVerified = verify(text, signature, masterKey, "UTF-8");
        if (!isVerified) {
            return false;
        }
        return true;
    }
	private boolean verify(String text, String sign, String key, String inputCharset) {
        text = text + key;
        String mysign = DigestUtils.md5Hex(getContentBytes(text, inputCharset));
        return mysign.equals(sign);
    }
	private byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

	/*
	 * 模拟支付确认
	 */
	@RequestMapping(value = "account/checkAccount/{}/", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String checkAccount(HttpServletRequest request,
			@PathVariable("money") int money,
			@PathVariable("channel") String channel,
			@RequestParam(value = "desc", required = false) String desc) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		if (user != null) {
			String uid = user.getId();
			String accountId = accountService.checkAccount(uid);
			if (accountId == null || accountId.equals("")) {
				result.put("msg", "用户尚未在平台开立账户");
				result.put("code", "-2");
			} else {
				TransactionRecord tr = new TransactionRecord();
				Date date = new Date();
				String trid = uid + date.getTime();
				tr.setTrid(trid);
				tr.setPaymentId(uid);
				tr.setReceiveId("1");
				tr.setDescription(desc);
				tr.setChannel(channel);
				tr.setMoney(money);
				tr.setPerformanceId("1");
				tr.setType(1);
				int ret = accountService.newTR(tr);
				if (ret == 1) {
					result.put("msg", "充值订单创建成功！");
					result.put("code", "1");
				} else {
					result.put("msg", "充值订单创建失败！");
					result.put("code", "0");
				}
			}
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}

	/*
	 * 提现
	 */
	@RequestMapping(value = "account/outAccount/{money}/{channel}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String outAccount(HttpServletRequest request,
			@PathVariable("money") int money,
			@PathVariable("channel") String channel,
			@RequestParam(value = "desc", required = false) String desc) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		if (user != null) {
			String uid = user.getId();
			String accountId = accountService.checkAccount(uid);
			if (accountId == null || accountId.equals("")) {
				result.put("msg", "用户尚未在平台开立账户");
				result.put("code", "-2");
			} else {
				TransactionRecord tr = new TransactionRecord();
				Date date = new Date();
				String trid = uid + date.toString();
				tr.setTrid(trid);
				tr.setPaymentId(uid);
				tr.setReceiveId("1");
				tr.setDescription(desc);
				tr.setChannel(channel);
				tr.setMoney(money);
				tr.setPerformanceId("1");
				tr.setType(1);
				int ret = accountService.newTR(tr);
				if (ret == 1) {
					result.put("msg", "充值订单创建成功！");
					result.put("code", "1");
				} else {
					result.put("msg", "充值订单创建失败！");
					result.put("code", "0");
				}

			}
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}

	/*
	 * insert
	 * accountCard(accountcardid,uid,cardno,bank,bandtime,ismaster,cardname)
	 * values(#{accountCardId},#{uid},#{cardNo},#{bank},now(),0,#{cardName})
	 */
	
	

	
	
	/*
	 * 读取用户的账单列表
	 */
	@RequestMapping(value = "account/getBills/{timelevel}/{page}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String getBills(HttpServletRequest request,
			@PathVariable("timelevel") int timelevel,
			@PathVariable("page") int page,
			@RequestParam(value="uid",required=false) String uid) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		if (user != null) {
			uid = user.getId();
			String startTime=null,endTime=null;
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date current = new Date();
			Date beforeTime = new Date();
			Calendar calendar = Calendar.getInstance(); //得到日历
			calendar.setTime(current);//把当前时间赋给日历
			
			switch(timelevel){
			case 1:
				startTime = simpleFormat.format(current);
				calendar.add(Calendar.MONTH, -1);  //设置为前1月
				beforeTime = calendar.getTime();   //得到前3月的时间
				endTime = simpleFormat.format(beforeTime);
				break;
			case 3:
				startTime = simpleFormat.format(current);
				calendar.add(Calendar.MONTH, -3);  //设置为前1月
				beforeTime = calendar.getTime();   //得到前3月的时间
				endTime = simpleFormat.format(beforeTime);
				break;
			case 6:
				startTime = simpleFormat.format(current);
				calendar.add(Calendar.MONTH, -6);  //设置为前1月
				beforeTime = calendar.getTime();   //得到前3月的时间
				endTime = simpleFormat.format(beforeTime);
				break;
			default:
			}
			//System.out.println("uid"+uid+",startTime:"+startTime+",endTime:"+endTime+",start:"+(page-1)*PAGE_LENGTH+",end:"+PAGE_LENGTH);
			List<Bill> billList = accountService.getBills(uid, startTime, endTime, (page-1)*PAGE_LENGTH, PAGE_LENGTH);
			result.put("bills", billList);
			result.put("msg", "访问成功");
			result.put("code", "1");
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 修改用户的支付密码，用原始支付密码进行修改
	 * (String uid, String password, String oldpd)
	 */
	@RequestMapping(value = "account/updatePassWord", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String updatePassWordOfAccount(HttpServletRequest request,
			@RequestParam(value="password") String password,
			@RequestParam(value="oldpd") String oldpd) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		if (user != null) {
			String uid = user.getId();
			String accountId = accountService.checkAccount(uid);
			System.out.println(accountId);
			if(!accountId.equals(null)&&!accountId.equals("")&&password.length()==6){
				int ret = accountService.updatePassWordOfAccount(uid, password,oldpd);
				if(ret==1){
					result.put("msg", "修改支付密码成功");
					result.put("code", "1");
				}else{
					result.put("msg", "原密码错误，修改支付密码失败！");
					result.put("code", "0");
				}
			}
			else{
				result.put("msg", "用户尚未建立帐户");
				result.put("code", "-2");
			}
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	
	/*
	 * 修改用户的支付密码，使用绑定的手机号接收验证码
	 */
	@RequestMapping(value = "account/updatePassWordByMobil", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String updatePassWordByMobil(HttpServletRequest request,
			@RequestParam(value="password") String password
			) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		if (user != null) {
			String uid = user.getId();
			String accountId = accountService.checkAccount(uid);
			if(!accountId.equals(null)&&!accountId.equals("")){
				String check = (String) session.getAttribute(AccountService.PAY_PASSWORD);
				if(check.equals("1")){
					int ret = accountService.updatePassWordOfAccount(uid, password,null);
					if(ret==1){
						result.put("msg", "修改支付密码成功");
						session.removeAttribute(AccountService.PAY_PASSWORD);
						result.put("code", "1");
					}else{
						result.put("msg", "修改支付密码失败！");
						result.put("code", "0");
					}
				}else{
					result.put("msg", "验证码错误");
					result.put("code", "-3");
				}
			}
			else{
				result.put("msg", "用户尚未建立帐户");
				result.put("code", "-2");
			}
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	/*
	 * 验证用户的原始支付密码是否正确
	 */
	@RequestMapping(value = "account/checkPassWord", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody
	String checkPassWord(HttpServletRequest request,
			@RequestParam(value="password") String password
			) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(UserService.LOGIN_USER);
		Map<String, Object> result = new HashMap<String, Object>();
		if (user != null) {
			String uid = user.getId();
			String accountId = accountService.checkAccountPassword(uid, password);
			if(!(accountId==null)&&!accountId.equals("")){
				result.put("msg", "密码正确");
				result.put("code", "1");
			}
			else{
				result.put("msg", "密码不正确");
				result.put("code", "-2");
			}
		} else {
			result.put("msg", "未登录");
			result.put("code", "-1");
		}
		return JSONObject.fromObject(result).toString();
	}
	
}

