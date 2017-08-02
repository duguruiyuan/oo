package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

public class CustomerServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	CustomerService customerService;
	
	/**
	 * @author HuangXiaodong
	 * 加入登陆日志代码test
	 * 
	 */
	@Test
  public void testRecordLoginLog(){
	  Map<String, Object> map=new HashMap<String, Object>();
	  map.put("custId", "409082584cc52a79014cc52a7ebe0000");
	  map.put("loginIp", "127.0.0.1");
	  customerService.recordLoginLog(map);
  }
	
	/**
	 * @author HuangXiaodong
	 * 用户活期宝信息test
	 */
	@SuppressWarnings("unchecked")
	@Test
  public void testFindBaoCountInfoByCustId(){
	  Map<String, Object> map=new HashMap<String, Object>();
	  map.put("custId", "d0367c22-0e89-44e6-ad9b-5d19a708de3d");
	  ResultVo reuslt = customerService.findBaoCountInfoByCustId(map);
	  Map<String, Object> mapResult = (Map<String, Object>)reuslt.getValue("data");
	  assertNotNull(mapResult);
  }	
	
	/**
	 * 
	 *
	 * @author  wangjf
	 * @date    2015年10月19日 下午6:39:21
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFindExperienceBaoCountInfoByCustId() {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("custId", "99c20930-df43-40ae-892a-9e64ab289dab");
		ResultVo reuslt = customerService.findExperienceBaoCountInfoByCustId(map);
	    Map<String, Object> mapResult = (Map<String, Object>)reuslt.getValue("data");
		assertNotNull(mapResult);
	}
	
	/**
	 * @author HuangXiaodong
	 * 用户善林理财信息test
	 */
	@Test
  public void testFindWealthCountInfoByCustId(){
	  Map<String, Object> map=new HashMap<String, Object>();
	  map.put("custId", "623d4018-d2fe-40be-ae9f-58dd8c4c4913");
	  customerService.findWealthCountInfoByCustId(map);
  }	
	
	/**
	 * @author HuangXiaodong 2015-04-23
	 * @param Map<String, Object>
	 *        custId: 用户id
	 *        tradeType：交易类型
	 *        starDate：查询开始时间
	 *        endDate：查询截止时间
	 * 活期宝-交易明细test
	 * @return ResultVo
	 */
	@Test
  public void testFindAllBaoAccountDetailByCustId(){
	  Map<String, Object> map=new HashMap<String, Object>();
	  map.put("start", 0);
	  map.put("length", 10);
	  map.put("custId", "7ee3e927-bb25-4527-b953-26b31bc92f2e");
	  map.put("productType", Constant.PRODUCT_TYPE_04);
	  //map.put("tradeType", "赎回活期宝");
	  customerService.findAllBaoAccountDetailByCustId(map);
  }	
	
	/**
	 * 测试获取累计收益
	 *
	 * @author  wangjf
	 * @date    2015年5月5日 上午11:02:50
	 */
	@Test
	public void testFindTotalIncomeByCustId() {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("custId", "961f8a6b-ca96-47e2-b6e7-97b700ddda34");
		param.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_01);
		customerService.findTotalIncomeByCustId(param);
	}
	
	/**
	 * 测试查询赎回记录
	 *
	 * @author  wangjf
	 * @date    2015年6月3日 下午5:22:27
	 */
	@Test
	public void testFindAllAtoneByCustId() {
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("custId", "20150427000000000000000000000000001");
		param.put("productType", Constant.PRODUCT_TYPE_01);
		customerService.findAllAtoneByCustId(param);
	}
	
	/**
	 * 查询客户基本信息和联系人基本信息(善林财富二期) 测试
	 * 
	 * @author  zhangzs
	 * @date    2015年7月9日 下午11:29:27
	 * @param custId
	 * @throws SLException
	 */
	@Test
	public void testGetCustAndContactCustInfo() throws SLException{
		Map<String,Object> params = Maps.newHashMap();
		params.put("custId", "de305554-1ba8-4129-95a7-386d978fbdc5");
		Map<String,Object> map = customerService.getCustAndContactCustInfo(params);
		System.out.println(map);
	}
	
	/**
	 * 修改账户用户信息和联系人信息(善林财富二期) 测试
	 * 
	 * @author  zhangzs
	 * @date    2015年7月9日 下午11:29:27
	 * @param custId
	 * @throws SLException
	 */
	@Test
	public void testPostCustAndContactCustInfo() throws SLException{
		Map<String,Object> custInfoMap = Maps.newHashMap();
		custInfoMap.put("id", "de305554-1ba8-4129-95a7-386d978fbdc5");
		custInfoMap.put("natvicePlaceProvince", "江苏省");
		custInfoMap.put("natvicePlaceCity", "南通市");
		custInfoMap.put("natvicePlaceCounty", "嘉里县");
		custInfoMap.put("communAddress", "同兴路"+System.currentTimeMillis()+"号");
		custInfoMap.put("qqCode", "987654321");
		Map<String,Object> contanctInfo = Maps.newHashMap();
		contanctInfo.put("id", "2");
		contanctInfo.put("contactName", "测试"+System.currentTimeMillis());
		contanctInfo.put("relationType", "同事"+System.currentTimeMillis());
		contanctInfo.put("contanctTelePhone", System.currentTimeMillis());
		custInfoMap.put("contanctInfo", contanctInfo);
		ResultVo result = customerService.updateCustAndContactCustInfo(custInfoMap);
		log.info(result.toString());
	}
	
	@Test
	public void testRegister() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		String mobile = "13621846167";
		String loginName = "wangjingfeng2001";
		String loginPassword = "a1!@#$%^&*()aA";
		params.put("mobile", mobile);
		params.put("loginName", loginName);
		params.put("loginPassword", loginPassword);
		params.put("messageType", Constant.SMS_TYPE_REGISTER);
		params.put("verityCode", "367532");
		//params.put("channelNo", "PD900610388");
		//params.put("inviteCode", "133333");
		params.put("channelNo", "2015070100000001");
		
		customerService.register(params);
	}
	
	@Test
	public void testRegisterNew() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		String mobile = "13621846169";
		String loginPassword = "a1!@#$%^&*()aA";
		params.put("mobile", mobile);
		params.put("loginPassword", loginPassword);
		params.put("messageType", Constant.SMS_TYPE_REGISTER);
		params.put("verityCode", "367532");
		params.put("noValidateCode", "Y");
		//params.put("channelNo", "PD900610388");
		//params.put("inviteCode", "133333");
		params.put("channelNo", "PD900610388");
		
		ResultVo result = customerService.registerNew(params);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void testUpdateUserNickName() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("custId", "afd8d441-a363-4e18-abe6-3813e66dcef5");
		params.put("newLoginName", "wangjf");
		ResultVo result = customerService.updateUserNickName(params);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void testFindBaoTradeInfo() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("custId", "20150427000000000000000000000000001");
		params.put("tradeDateBegin", "2015-8-1");
		params.put("tradeDateEnd", "2016-2-1");
		ResultVo result = customerService.findBaoTradeInfo(params);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	
//	@Test
//	public void synchronizeCustInfoAndInvestInfoFromWealthToSlcf() throws SLException{
//		ResultVo result = customerService.synchronizeCustInfoAndInvestInfoFromWealthToSlcf();
//		assertEquals(ResultVo.isSuccess(result), true);
//	}
}
