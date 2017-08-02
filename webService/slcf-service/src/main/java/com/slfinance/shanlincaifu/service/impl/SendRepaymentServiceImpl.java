package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.SendRepaymentCustom;
import com.slfinance.shanlincaifu.service.SendRepaymentService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("sendRepaymentService")
public class SendRepaymentServiceImpl implements SendRepaymentService {
	
	private RestTemplate client = new RestTemplate(); 

	@Value(value = "${server.port}")
	private String serverPort;
	
	@Autowired
	SendRepaymentCustom sendRepaymentCustom;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	

	@Override
	public ResultVo sendRepayment(Map<String, Object> param) throws SLException {
		List<Map<String, Object>> list = sendRepaymentCustom.querySendMessageList();
		
		int success = 0;
		for(Map<String, Object> m : list) {
			try {
				sendMessage(m.get("loanCode").toString(), Integer.parseInt(m.get("currentTerm").toString()), m.get("expectRepaymentDate").toString());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				log.warn(e.getMessage());
			}
			success ++;
		}
		log.info(String.format("共%d条, 成功%d条", list.size(), success));
		
		return new ResultVo(true, "操作成功");
	}
	
	private void sendMessage(String loanNo, Integer currentTerm, String expectRepaymentDate) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanNo", loanNo); //SXRZ-0004
		param.put("requestTime", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		param.put("companyName", "善信融资");
		param.put("tradeCode", "SXRZ-TRADE-" + DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
		param.put("repaymentStatus", "正常还款");
		param.put("penaltyAmount", 0);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalRepaymentTerms", 1);
		
		List<Map<String, Object>> repaymentList = Lists.newArrayList();
		Map<String, Object> repaymentMap1 = new HashMap<String, Object>();
		repaymentMap1.put("currentTerm", currentTerm);
		repaymentMap1.put("expectRepaymentDate", expectRepaymentDate);
		repaymentMap1.put("penaltyInterest", 0);
		repaymentList.add(repaymentMap1);
		resultMap.put("repaymentList", repaymentList);
		
		param.put("result", resultMap);
		
		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(Constant.NOTIFY_LOAN_COMPANY_01, Constant.NOTIFY_TYPE_LOAN_REPAYMENT);
		String hashString = interfaceDetailInfoEntity.getSecretKey() + (String)param.get("companyName") + (String)param.get("loanNo") 
				+ (String)param.get("requestTime");
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		param.put("sign", sign);
		System.out.println(Json.ObjectMapper.writeValue(param));
		// 发送请求
		String url = "http://localhost:" + serverPort + "/openservice/repayment";
//		String url = "http://localhost:" + serverPort + "/user/getUserInfo";
//		param.put("mobile", "18000000000");
		@SuppressWarnings("unchecked")
		Map<String, Object> result = client.postForObject(url, param, Map.class);
		System.out.println(Json.ObjectMapper.writeValue(result));
	}
	
}
