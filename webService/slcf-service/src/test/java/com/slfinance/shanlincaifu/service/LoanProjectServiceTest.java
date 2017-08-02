package com.slfinance.shanlincaifu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Json;

public class LoanProjectServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private LoanProjectService loanProjectService;
	
	/**
	 * 测试保存
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 上午11:14:07
	 * @throws SLException
	 */
	@Test
	public void testSaveLoan() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanNo", "SXRZ-0005");
		param.put("requestTime", "20161201111111");
		param.put("applyTime", "20161201111111");
		param.put("companyName", "善信融资");
		param.put("tradeCode", "SXRZ-TRADE-0005");
		param.put("loanType", "精英贷");
		param.put("loanDesc", "买房紧急挪用");
		param.put("loanAmount", 60000);
		param.put("loanTerm", 6);
		param.put("termUnit", "月");
		param.put("yearIrr", 0.08);
		param.put("repaymentMethod", "等额本息");
		param.put("repaymentCycle", "1");
		param.put("carType", "本田");
		param.put("propertyRight", "SXRZ-0001");
		param.put("houseType", "商品住宅");
		
		Map<String, Object> custMap = new HashMap<String, Object>();
		custMap.put("mobile", "18521509557");
		custMap.put("custName", "张三");
		custMap.put("credentialsType", "身份证");
		custMap.put("credentialsCode", "320683198702214331");
		custMap.put("custCode", "SXRZ-CUST-0001");
		custMap.put("education", "大学本科");
		custMap.put("marriage", "已婚");
		custMap.put("homeAddress", "上海浦东新区川沙镇");
		custMap.put("workCorporation", "善林金融信息服务有限公司");
		custMap.put("workAddress", "上海浦东新区祖冲之路2001号");
		custMap.put("workTelephone", "021-869587891");
		custMap.put("workYear", "3");
		custMap.put("salaryType", "待发");
		param.put("custMap", custMap);
		
		Map<String, Object> bankMap = new HashMap<String, Object>();
		bankMap.put("bankName", "浦东银行");
		bankMap.put("cardNo", "6257511001125415869");
		param.put("bankMap", bankMap);
		
		List<Map<String, Object>> auditList = Lists.newArrayList();
		Map<String, Object> auditMap1 = new HashMap<String, Object>();
		auditMap1.put("auditType", "身份证");
		auditMap1.put("fileName", "正面");
		auditMap1.put("filePath", "/loan/sfex01.jpg");
		auditMap1.put("auditUser", "李四");
		auditMap1.put("auditDate", "20161201111111");
		auditList.add(auditMap1);
		
		Map<String, Object> auditMap2 = new HashMap<String, Object>();
		auditMap2.put("auditType", "身份证");
		auditMap2.put("fileName", "正面");
		auditMap2.put("filePath", "/loan/sfex02.jpg");
		auditMap2.put("auditUser", "李四");
		auditMap2.put("auditDate", "20161201111111");
		auditList.add(auditMap2);
		
		param.put("auditMap", auditList);
		
		List<Map<String, Object>> repaymentList = Lists.newArrayList();
		Map<String, Object> repaymentMap1 = new HashMap<String, Object>();
		repaymentMap1.put("currentTerm", 1);
		repaymentMap1.put("expectRepaymentDate", "20161201");
		repaymentMap1.put("repaymentTotalAmount", 10400);
		repaymentMap1.put("repaymentPrincipal", 10000);
		repaymentMap1.put("repaymentInterest", 400);
		repaymentMap1.put("advanceCleanupTotalAmount", 60000);
		repaymentList.add(repaymentMap1);
		
		Map<String, Object> repaymentMap2 = new HashMap<String, Object>();
		repaymentMap2.put("currentTerm", 2);
		repaymentMap2.put("expectRepaymentDate", "20170101");
		repaymentMap2.put("repaymentTotalAmount", 10400);
		repaymentMap2.put("repaymentPrincipal", 10000);
		repaymentMap2.put("repaymentInterest", 400);
		repaymentMap2.put("advanceCleanupTotalAmount", 50000);
		repaymentList.add(repaymentMap2);
		
		Map<String, Object> repaymentMap3 = new HashMap<String, Object>();
		repaymentMap3.put("currentTerm", 3);
		repaymentMap3.put("expectRepaymentDate", "20170201");
		repaymentMap3.put("repaymentTotalAmount", 10400);
		repaymentMap3.put("repaymentPrincipal", 10000);
		repaymentMap3.put("repaymentInterest", 400);
		repaymentMap3.put("advanceCleanupTotalAmount", 40000);
		repaymentList.add(repaymentMap3);
		
		Map<String, Object> repaymentMap4 = new HashMap<String, Object>();
		repaymentMap4.put("currentTerm", 4);
		repaymentMap4.put("expectRepaymentDate", "20170301");
		repaymentMap4.put("repaymentTotalAmount", 10400);
		repaymentMap4.put("repaymentPrincipal", 10000);
		repaymentMap4.put("repaymentInterest", 400);
		repaymentMap4.put("advanceCleanupTotalAmount", 30000);
		repaymentList.add(repaymentMap4);
		
		Map<String, Object> repaymentMap5 = new HashMap<String, Object>();
		repaymentMap5.put("currentTerm", 5);
		repaymentMap5.put("expectRepaymentDate", "20170401");
		repaymentMap5.put("repaymentTotalAmount", 10400);
		repaymentMap5.put("repaymentPrincipal", 10000);
		repaymentMap5.put("repaymentInterest", 400);
		repaymentMap5.put("advanceCleanupTotalAmount", 30000);
		repaymentList.add(repaymentMap5);
		
		Map<String, Object> repaymentMap6 = new HashMap<String, Object>();
		repaymentMap6.put("currentTerm", 6);
		repaymentMap6.put("expectRepaymentDate", "20170501");
		repaymentMap6.put("repaymentTotalAmount", 10400);
		repaymentMap6.put("repaymentPrincipal", 10000);
		repaymentMap6.put("repaymentInterest", 400);
		repaymentMap6.put("advanceCleanupTotalAmount", 30000);
		repaymentList.add(repaymentMap6);
		
		param.put("repaymentList", repaymentList);
		
		
		String hashString = "7149D7A3EDE945F54A87D1F49C8979ED" + (String)param.get("loanNo") + (String)param.get("requestTime") 
				+ (String)param.get("companyName") + (String)param.get("tradeCode");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		
		param.put("sign", sign);
	
		System.out.println(Json.ObjectMapper.writeValue(param));
	}
	
	/**
	 * 测试查询项目
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 下午1:09:20
	 * @throws SLException
	 */
	@Test
	public void testQueryProject() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanNo", "SXRZ-0001");
		param.put("requestTime", "20161201111111");
		param.put("companyName", "善信融资");
		param.put("tradeCode", "SXRZ-TRADE-10001");
		
		String hashString = "7149D7A3EDE945F54A87D1F49C8979ED" + (String)param.get("loanNo") + (String)param.get("requestTime") 
				+ (String)param.get("companyName") + (String)param.get("tradeCode");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		param.put("sign", sign);
		
		System.out.println(Json.ObjectMapper.writeValue(param));
	}
	
	/**
	 * 测试还款
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 下午1:09:30
	 * @throws SLException
	 */
	@Test
	public void testRepayment() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanNo", "SXRZ-0023"); //SXRZ-0004
		param.put("requestTime", "20161201111111");
		param.put("companyName", "善信融资");
		param.put("tradeCode", "SXRZ-TRADE-20015");
		param.put("repaymentStatus", "正常还款");
		param.put("penaltyAmount", 0);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalRepaymentTerms", 1);
		
		List<Map<String, Object>> repaymentList = Lists.newArrayList();
		Map<String, Object> repaymentMap1 = new HashMap<String, Object>();
		repaymentMap1.put("currentTerm", 2);
		repaymentMap1.put("expectRepaymentDate", "20161206");
		repaymentMap1.put("penaltyInterest", 0);
		repaymentList.add(repaymentMap1);
		resultMap.put("repaymentList", repaymentList);
		
		param.put("result", resultMap);
		
		String hashString = "7149D7A3EDE945F54A87D1F49C8979ED" + (String)param.get("companyName") + (String)param.get("loanNo") 
				+ (String)param.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		param.put("sign", sign);
		
		System.out.println(Json.ObjectMapper.writeValue(param));
	}
	
	/**
	 * 实名认证
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 下午1:14:01
	 * @throws SLException
	 */
	@Test
	public void testQueryRealName() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("requestTime", "20161201111111");
		param.put("custName", "惠亮亮");
		param.put("credentialsCode", "61273119920113323X");
		param.put("tradeCode", "SXRZ-TRADE-30001");
		
		String hashString = "7149D7A3EDE945F54A87D1F49C8979ED" + (String)param.get("custName") + (String)param.get("credentialsCode") 
				+ (String)param.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		param.put("sign", sign);
		
		System.out.println(Json.ObjectMapper.writeValue(param));
	}
	
	/**
	 * 绑卡
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 下午1:16:27
	 * @throws SLException
	 */
	@Test
	public void testBindCard() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tradeType", "绑卡");
		param.put("requestTime", "20161201111111");
		param.put("custName", "惠亮亮");
		param.put("credentialsCode", "61273119920113323X");
		param.put("custCode", "0152687");
		param.put("tradeCode", "SXRZ-TRADE-40001");
		param.put("bankName", "浦东银行");
		param.put("bankCardNo", "6257511001125415869");
		param.put("responseCardId", "21458078");
		
		String hashString = "7149D7A3EDE945F54A87D1F49C8979ED" + (String)param.get("custName") + (String)param.get("credentialsCode") 
				+ (String)param.get("custCode") + (String)param.get("bankCardNo") + (String)param.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		param.put("sign", sign);
		
		System.out.println(Json.ObjectMapper.writeValue(param));
	}
	
	/**
	 * 测试查询项目
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 下午1:09:20
	 * @throws SLException
	 */
	@Test
	public void testQueryProtocal() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanNo", "SXRZ-NEW-0035");
		param.put("requestTime", "20161201111111");
		param.put("companyName", "善信融资");
		param.put("tradeCode", "SXRZ-TRADE-50002");
		
		String hashString = "7149D7A3EDE945F54A87D1F49C8979ED" + (String)param.get("loanNo") + (String)param.get("companyName")
				 + (String)param.get("tradeCode") + (String)param.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		param.put("sign", sign);
		
		System.out.println(Json.ObjectMapper.writeValue(param));
	}
}
