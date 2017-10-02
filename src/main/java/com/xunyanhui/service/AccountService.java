/**
 * 创建日期：2017-1-11下午4:01:16
 * 修改日期：
 * 作者：邢传军
 */
package com.xunyanhui.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.xunyanhui.bean.AccountBalance;
import com.xunyanhui.bean.AccountCardL;
import com.xunyanhui.bean.Bill;
import com.xunyanhui.model.Account;
import com.xunyanhui.model.AccountCard;
import com.xunyanhui.model.TransactionRecord;

public interface AccountService {
	
	public final static String PAY_PASSWORD = "pay_password";

	/**
	 * 新建用户账户 
	 * @param account
	 * @return
	 */
	public int newAccount(Account account);
	/**
	 * 读取指定账户的余额账户已uid为id 
	 * @param uid				用户id
	 * @return
	 */
	public AccountBalance getBalanceOfUser(String uid);
	/**
	 * 检查用户是否存在帐户 
	 * @param uid				用户id
	 * @return   用户的账户id
	 */
	public String checkAccount(String uid);
	/**
	 * 充值向用户的系统帐户中充值 
	 * @param uid				用户id
	 * @return   用户的账户id
	 */
	public int newTR(TransactionRecord tr);
	/**
	 * 修改交易状态 
	 * @param trid				交易id
	 * @param state				state
	 * @return   用户的账户id
	 */
	public int updateTRState(String trid,int state);
	/**
	 * 绑定银行卡到用户账户上 
	 */
	public int bandCardOnAccount(AccountCard ac);
	/**
	 * 解除银行卡在用户账户上的绑定 
	 * @param uid				用户id
	 * @param cardno			银行卡卡号				
	 * @return
	 */
	public int unbandCardOnAccount(String uid,String cardno);
	/**
	 * 解除银行卡在用户账户上的绑定 
	 * @param uid				用户id
	 * @param cardno			银行卡卡号				
	 * @return
	 */
	public int setMasterCardOfAccount(String uid,String cardno);
	
	/**
	 * 读取用户绑定的银行卡列表 
	 * @param uid				用户id
	 * @param cardno			银行卡卡号				
	 * @return
	 */
	public List<AccountCardL> bandCardList(String uid);
	/**
	 * 检查银行卡是否绑定过 
	 * @param cardno			银行卡卡号				
	 * @return
	 */
	public AccountCard checkCardBand(String cardno);
	/**
	 * 获取客户的交易帐单
	 * @param uid				用户的id	
	 * @param satrtTime			账单过滤的开始时间	
	 * @param endTime			账单过滤的结束时间
	 * @param start				读取的账单开始位置
	 * @param length			读取的账单数量		
	 * @return
	 */
	public List<Bill> getBills(String uid,String startTime,String endTime,int start,int length);
	/**
	 * 设置用户的支付密码
	 * @param uid				用户的id	
	 * @param password			新支付密码	
	 * @param oldpd				原有支付密码
	 * @return
	 */
	public int updatePassWordOfAccount(String uid,String password,String oldpd);
	/**
	 * 获取用户绑定的移动手机号
	 * @param uid				用户id
	 * @return   用户的账户id
	 */
	public String getMobile(String uid);
	/**
	 * 检查用户的账户支付密码是否正确
	 * @param uid					用户id
	 * @param password				用户的支付密码
	 * @return   用户的账户id
	 */
	public String checkAccountPassword(String uid,String password);
	/**
	 * 获取指定交易详情
	 * @param trid					trid交易编号
	 */
	public TransactionRecord getTransactionById(String trid);
	/**
	 * 修改指定账户的余额
	 * @param uid					用户id
	 * @param money					更改余额(如果该数值为正，则余额增加；如果该数值为负，则余额减少)
	 */
	public Integer updateBanlance(String uid,Integer money);
}
