package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

public interface BizExtractDataCustom {

	/**
	 * @author liyy
	 * @return 
	 * @date 2017/3/17 10:57:30
	 * 邮件发送平台每日数据汇总<br>
	 * <tt>日充值金额(元)  日提现金额(元)  日投资金额(元)  日发布金额(元)  日转让金额(元)  日新增注册用户数</tt><br>
	 */
	List<Map<String, Object>> dailyDataSummary(Map<String, Object> params);
	
	/**
	 * 邮件发送平台每日推送数据汇总<br>
	 * @author guoyk
	 * @date 2017/4/25 14:57:30
	 * <tt>客户名称  手机号  投资日期  投资金额  投资状态  投资标的</tt><br>
	 */
	List<Map<String, Object>> dailyDataPropellingSummary(Map<String, Object> params);
	
	/**
	 * @author sunht
	 * @date 2017年6月16日 下午10:27:00
	 * <tt>"借款编号", "公司名称", "借款金额", "放款状态", "放款时间", "推标时间"</tt><br>
	 */
	public List<Map<String, Object>> dailyDataYZloanAccountSummary(Map<String, Object> params);

}
