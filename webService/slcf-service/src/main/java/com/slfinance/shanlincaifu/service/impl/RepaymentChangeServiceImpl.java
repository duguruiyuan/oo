package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.entity.WithholdAccountEntity;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.repository.WithholdAccountRepository;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.utils.*;
import com.slfinance.shanlincaifu.vo.withHoldingRequet.*;
import com.slfinance.thirdpp.util.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.RepaymentPlanCopeEntity;
import com.slfinance.shanlincaifu.service.RepaymentChangeService;
import com.slfinance.shanlincaifu.service.RepaymentPlanCopeService;
import com.slfinance.vo.ResultVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestOperations;

@Slf4j
@Service("repaymentChangeServiceImpl")
public class RepaymentChangeServiceImpl implements RepaymentChangeService{
	
	@Autowired
	private RepaymentPlanCopeService repaymentPlanCopeService;

	@Autowired
	private InnerRepaymentPlanChangeBusiness innerRepaymentPlanChangeBusiness;

	@Autowired
	private WithholdAccountRepository withholdAccountRepository;

	@Autowired
	private FlowNumberService flowNumberService;

	@Autowired
	private TradeLogInfoRepository tradeLogInfoRepository;

	/****
	 * 变更还款计划到数据库
	 * @param paramMap
	 * @return
	 * @throws SLException
	 */
	public ResultVo repaymentChange(Map<String, Object> paramMap,List<Map<String,Object>> repaymentList) throws SLException{

		//根据Json字符串，构建新的还款计划
		List<RepaymentPlanCopeEntity> newPlanList = new ArrayList<RepaymentPlanCopeEntity>();
		for (Map<String,Object> map : repaymentList){
			RepaymentPlanCopeEntity plan = new RepaymentPlanCopeEntity();
			plan.setAdvanceCleanupTotalAmount(new BigDecimal(map.get("advanceCleanupTotalAmount").toString()));
			plan.setCurrentTerm(Integer.parseInt(map.get("currentTerm").toString()));
			plan.setExpectRepaymentDate(map.get("expectRepaymentDate").toString());
			plan.setRepaymentInterest(new BigDecimal(map.get("repaymentInterest").toString()));
			plan.setRepaymentPrincipal(new BigDecimal(map.get("repaymentPrincipal").toString()));
			plan.setRepaymentTotalAmount(new BigDecimal(map.get("repaymentTotalAmount").toString()));
			newPlanList.add(plan);
		}

		List<RepaymentPlanCopeEntity> oldPlanList =repaymentPlanCopeService.queryByLoanCode(paramMap.get("loanNo").toString());
		List<RepaymentPlanCopeEntity> removeList = new ArrayList<RepaymentPlanCopeEntity>();
		for (RepaymentPlanCopeEntity oldPlan: oldPlanList ){
			for (RepaymentPlanCopeEntity newPlan : newPlanList){
				if (oldPlan.getChangeEnable().equals(WithHoldingConstant.Constant_NO)){
					removeList.add(oldPlan);
					continue;
				}
				if (oldPlan.getCurrentTerm()==newPlan.getCurrentTerm()){
					oldPlan.setAdvanceCleanupTotalAmount(newPlan.getAdvanceCleanupTotalAmount());
					oldPlan.setExpectRepaymentDate(newPlan.getExpectRepaymentDate());
					oldPlan.setRepaymentInterest(newPlan.getRepaymentInterest());
					oldPlan.setRepaymentPrincipal(newPlan.getRepaymentPrincipal());
					oldPlan.setRepaymentTotalAmount(newPlan.getRepaymentTotalAmount());
				}

			}
		}
		oldPlanList.removeAll(removeList);//移除已处理的 还款计划
		List<ThirdRepaymentPlanVo> planList = new ArrayList<ThirdRepaymentPlanVo>();
		for (RepaymentPlanCopeEntity entity: oldPlanList){
			ThirdRepaymentPlanVo plan = new ThirdRepaymentPlanVo();
			try {
				plan.setDate(DateUtils.formatDate(new SimpleDateFormat("yyyyMMdd")
                        .parse(entity.getExpectRepaymentDate()),"yyyy-MM-dd"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			plan.setAmount(entity.getRepaymentTotalAmount().toString());
			planList.add(plan);
		}


		WithholdAccountEntity withholdAccount = withholdAccountRepository.findByCompanyName(paramMap.get("companyName").toString());
		ThirdSmsParamVo smsParam = new ThirdSmsParamVo();
		smsParam.setContract_type(withholdAccount.getContractType());
		smsParam.setContact_way(withholdAccount.getContactWay());
		String batchCode = flowNumberService.generateTradeBatchNumber();
		String requestNo = flowNumberService.generateAllotNumber();
		String requestTime = DateUtils.getCurrentDate("yyyyMMddHHmmss");
		Map<String ,Object> requestMap = new HashMap<String ,Object>();
		requestMap.put("batchCode",batchCode);
		requestMap.put("requestNo",requestNo);
		requestMap.put("requestTime",DateUtils.getCurrentDate("yyyyMMddHHmmss"));

		Map<String,Object> planMap = Maps.newConcurrentMap();
		planMap.put("repaymentPlan",planList);
		requestMap.put("repaymentPlan",Json.ObjectMapper.writeValue(planMap));

		requestMap.put("repaymentNo",paramMap.get("loanNo").toString());
		requestMap.put("smsParam",smsParam);

		ResultVo resultVo = innerRepaymentPlanChangeBusiness.thirdChangePlan(requestMap);

		TradeLogInfoEntity tradeLogInfo = new TradeLogInfoEntity();
		tradeLogInfo.setTradeCode(batchCode);
		tradeLogInfo.setInterfaceType(Constant.REPAYMENT_CHANGE);
		tradeLogInfo.setThirdPartyType(paramMap.get("companyName").toString());
		tradeLogInfo.setRequestTime(requestTime);
		tradeLogInfo.setRequestMessage(Json.ObjectMapper.writeValue(requestMap));
		tradeLogInfo.setResponseMessage(Json.ObjectMapper.writeValue(resultVo.getValue("data")));
		tradeLogInfoRepository.save(tradeLogInfo);
		//变更还款计划副本
		repaymentPlanCopeService.updatePlan(paramMap.get("loanNo").toString(),oldPlanList);
		return resultVo;
		
	}



	@Service
	public static class InnerRepaymentPlanChangeBusiness{

		@Autowired
		@Qualifier("thirdPartyPayRestClientService")
		private RestOperations slRestClient;

		@Value("${REPAYMENT_CHANGE_REQUEST_URL}")
		private String repaymentPlanChangeUrl ;

		/****
		 * 请求第三方变更
		 * @param requetMap
		 * @return
		 */
		public ResultVo thirdChangePlan(Map<String ,Object> requetMap){

			//构建请求对象
			BaseRequestVo<RepaymentChangeRequestVo> requestVo =new BaseRequestVo<RepaymentChangeRequestVo>();
			requestVo.setBatchCode(requetMap.get("batchCode").toString());
			requestVo.setBuzName(Constant.DEBT_SOURCE_CODE_SLCF);
			requestVo.setPlatform(WithHoldingConstant.PLATFORM);
			requestVo.setRequestTime(requetMap.get("requestTime").toString());
			requestVo.setServiceName(WithHoldingConstant.REPAYMENT_CHANGE_SERVICE_NAME);
			requestVo.setUserDevice(WithHoldingConstant.USER_DEVICE_PC);
			RepaymentChangeRequestVo changeVo = new RepaymentChangeRequestVo();
			changeVo.setRepaymentNo(requetMap.get("repaymentNo").toString());
			//changeVo.setPlatformUserNo(requetMap.get("userNo").toString());
			changeVo.setPlatformUserNo("00001");
			changeVo.setRequestNo(requetMap.get("requestNo").toString());
			changeVo.setRepaymentPlan(requetMap.get("repaymentPlan").toString());
			changeVo.setSmsParam(Json.ObjectMapper.writeValue(requetMap.get("smsParam")));
			requestVo.setReqData(changeVo);

			try {
				HttpHeaders headers = new HttpHeaders();
				MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
				headers.setContentType(type);
				headers.add("Accept", MediaType.APPLICATION_JSON.toString());
				Map<String,String> requestMap = ShareUtil.jsonToMap(Json.ObjectMapper.writeValue(requestVo));
				HttpEntity<Map<String,String>> formEntity = new HttpEntity<Map<String,String>>(requestMap,headers);

				Map<String,String> result = slRestClient.postForObject(repaymentPlanChangeUrl,formEntity,Map.class);
				BaseResponseVo responseVo = (BaseResponseVo)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(result),BaseResponseVo.class);
				RepaymentChangeRespVo changeRespVo = (RepaymentChangeRespVo)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(responseVo.getRespData()),RepaymentChangeRespVo.class);
				if (changeRespVo.getCode()!=null && changeRespVo.getCode().equals(WithHoldingConstant.RESPONSE_SUCCESS_CODE)){
					return new ResultVo(true,"还款计划变更成功",responseVo);
				}
				return new ResultVo(false,"还款计划变更失败",responseVo);
			}catch (Exception e){
				log.info("请求还款计划变更第三方异常..............."+e.getMessage());
				e.printStackTrace();
				return new ResultVo(false,"还款计划变更失败",e.getMessage());
			}


		}

	}

}
