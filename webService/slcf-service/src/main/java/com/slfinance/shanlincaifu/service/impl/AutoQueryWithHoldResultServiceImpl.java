package com.slfinance.shanlincaifu.service.impl;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.WithHoldingFlowEntity;
import com.slfinance.shanlincaifu.repository.WithHoldingFlowRepository;
import com.slfinance.shanlincaifu.service.AutoQueryWithHoldResultService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.WithHoldingService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.shanlincaifu.utils.WithHoldingConstant;
import com.slfinance.shanlincaifu.vo.withHoldingRequet.*;
import com.slfinance.thirdpp.util.ShareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.util.List;
import java.util.Map;

/**
 * <p><b>标题：</b>webService</p>
 * <p><b>描述：</b>定时查询扣款结果Service </p>
 * <p><b>公司：</b>善林财富 </p>
 * <p><b>版权声明：</b>Copyright (c) 2017</p>
 * @author 张祥
 * @date 2017/7/17 16:08
 */
@Slf4j
@Service
public class AutoQueryWithHoldResultServiceImpl implements AutoQueryWithHoldResultService {

    @Autowired
    private WithHoldingFlowRepository withHoldingFlowRepository;

    @Autowired
    private FlowNumberService flowNumberService;

    @Autowired
    private WithHoldingService withHoldingService;

    @Autowired
    @Qualifier("thirdPartyPayRestClientService")
    private RestOperations slRestClient;

    @Value("${WITH_HOLDING_QUERY_URL}")
    private String withHoldingRequestQueryUrl;

    @Override
    public void queryTppWithholdingResult() throws SLException {
        List<WithHoldingFlowEntity> flowList = withHoldingFlowRepository.findByIsNeedQuery(WithHoldingConstant.Constant_YES);
        log.info("查询到需要确认扣款结果的数据条数为："+flowList.size()+"====================");
        if (flowList != null && flowList.size()>0){//存在需要去查询结果的 扣款交易
            for (WithHoldingFlowEntity flow : flowList){
                BaseRequestVo<WithholdingResultReqVo> requestVo= new BaseRequestVo<WithholdingResultReqVo>();
                String batchCode = flowNumberService.generateTradeBatchNumber();
                String requestNo = flowNumberService.generateAllotNumber();
                requestVo.setBatchCode(batchCode);
                requestVo.setBuzName(Constant.DEBT_SOURCE_CODE_SLCF);
                requestVo.setPlatform(WithHoldingConstant.PLATFORM);
                requestVo.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
                requestVo.setUserDevice(WithHoldingConstant.USER_DEVICE_PC);
                requestVo.setServiceName(WithHoldingConstant.WITH_HOLDING_QUERY_SERVICE_NAME);

                WithholdingResultReqVo queryVo = new WithholdingResultReqVo();
                queryVo.setBizBatchCode(flow.getBatchCode());
                queryVo.setRequestNo(requestNo);
                requestVo.setReqData(queryVo);
                try {
                    HttpHeaders headers = new HttpHeaders();
                    MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                    headers.setContentType(type);
                    headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                    Map<String,String> requestMap = ShareUtil.jsonToMap(Json.ObjectMapper.writeValue(requestVo));
                    HttpEntity<Map<String,String>> formEntity = new HttpEntity<Map<String,String>>(requestMap,headers);
                    Map<String,String> result = slRestClient.postForObject(withHoldingRequestQueryUrl, formEntity,Map.class);
                    BaseResponseVo responseVo = (BaseResponseVo)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(result),BaseResponseVo.class);
                    ThidPayWithHoldRespVo queryResoVo = ( ThidPayWithHoldRespVo)ShareUtil.jsonToObject(Json.ObjectMapper.writeValue(responseVo.getRespData()),ThidPayWithHoldRespVo.class);
                    Map<String,Object> paramMap = Maps.newConcurrentMap();
                    paramMap.put("batchCode",flow.getBatchCode());
                    paramMap.put("requestNo",flow.getRequestNo());
                    paramMap.put("repaymentNo",queryResoVo.getRepaymentNo());
                    paramMap.put("moneyOrder",queryResoVo.getMoneyOrder());
                    paramMap.put("repaymentDate",queryResoVo.getRepaymentDate());
                    paramMap.put("code",queryResoVo.getCode());
                    paramMap.put("status",queryResoVo.getStatus());
                    paramMap.put("isLimit",queryResoVo.getIsLimit());
                    paramMap.put("repaymentTerm",queryResoVo.getRepaymentTerm());
                    paramMap.put("tradeAmout",queryResoVo.getMoneyOrder());
                    paramMap.put("status",queryResoVo.getStatus());
                    if(queryResoVo.getCode().equals(WithHoldingConstant.RESPONSE_SUCCESS_CODE)){
                        withHoldingService.withHoldingSuccess(paramMap);
                    }else {
                        if (queryResoVo.getIsLimit().equals(WithHoldingConstant.Constant_NO)){
                            withHoldingService.withHoldingFailed(paramMap);
                        }else{
                        	withHoldingService.timeLimitWithHoldingFailed(paramMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
