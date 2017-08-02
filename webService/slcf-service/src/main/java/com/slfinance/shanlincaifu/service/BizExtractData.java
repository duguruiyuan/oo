package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.vo.ResultVo;

/**
 * 业务抽取数据接口
 * @author liyy
 * @date 2017/3/17 10:57:30
 */
public interface BizExtractData {

	/**
	 * 邮件发送平台每日数据汇总<br>
	 * @author liyy
	 * @date 2017/3/17 10:57:30
	 * <tt>日充值金额(元)  日提现金额(元)  日投资金额(元)  日发布金额(元)  日转让金额(元)  日新增注册用户数</tt><br>
	 */
	public ResultVo dailyDataSummary(Map<String, Object> params);
	
	/**
	 * 邮件发送平台每日推送数据汇总<br>
	 * @author guoyk
	 * @date 2017/4/25 14:57:30
	 * 
	 * <tt>客户名称  手机号  投资日期  投资金额  投资状态  投资标的</tt><br>
	 */
	public ResultVo dailyDataPropellingSummary(Map<String, Object> params);
	
	
	/**
	 * 邮件发送意真放款平台每日推送数据
	 * @author sunht
	 * @date 2017年6月16日 下午10:27:00
	 * <tt>"借款编号", "公司名称", "借款金额", "放款状态", "放款时间", "推标时间"</tt><br>
	 */
	public ResultVo dailyDataYZloanAccountSummary(Map<String, Object> params);
}
