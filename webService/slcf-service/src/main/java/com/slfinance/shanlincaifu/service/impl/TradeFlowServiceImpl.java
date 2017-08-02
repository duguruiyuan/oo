package com.slfinance.shanlincaifu.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.searchFilter.DynamicSpecifications;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter.Operator;
import com.slfinance.shanlincaifu.service.TradeFlowService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

@Service("tradeFlowService")
@Transactional(readOnly = true)
public class TradeFlowServiceImpl implements TradeFlowService {
	
	final TradeFlowInfoRepository TradeFlowInfoRepository;
	
	@Autowired
	public TradeFlowServiceImpl(TradeFlowInfoRepository repository) {
		this.TradeFlowInfoRepository = repository;
	}
	
	@Override
	public Page<TradeFlowInfoEntity> findTradeFlowInfoPagable(Map<String, Object> param) {
		int pageNumber = 0;
		int pageSize = 2;
		if(!StringUtils.isEmpty(param.get("pageNumber")) && !StringUtils.isEmpty(param.get("pageSize"))) {
			pageNumber = Integer.parseInt(param.get("pageNumber")+"");
			pageSize = Integer.parseInt(param.get("pageSize")+"");
		}
		PageRequest pageRequest = new PageRequest(pageNumber, pageSize,
				new Sort(Direction.DESC, "createDate"));
		
		List<SearchFilter> filters = Lists.newArrayList();
		if (!StringUtils.isEmpty(param.get("custId")))
			filters.add(new SearchFilter("custId", Operator.EQ, param.get("custId")));
		
		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			if(SubjectConstant.TRADE_FLOW_TYPE_RECHARGE.equals((String)param.get("tradeType"))) {
				filters.add(new SearchFilter("tradeType", Operator.IN,
						Arrays.asList(Constant.OPERATION_TYPE_42, SubjectConstant.TRADE_FLOW_TYPE_RECHARGE)));
			}
			else if(SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW.equals((String)param.get("tradeType"))) {
				filters.add(new SearchFilter("tradeType", Operator.IN,
						Arrays.asList(Constant.OPERATION_TYPE_43, SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW)));
			}
			else {
				filters.add(new SearchFilter("tradeType", Operator.EQ,
						param.get("tradeType")));
			}
		}
			
		if (!StringUtils.isEmpty(param.get("tradeStatus")))
			filters.add(new SearchFilter("tradeStatus", Operator.EQ,
					param.get("tradeStatus")));
		
		if (param.get("beginDate") != null)
			filters.add(new SearchFilter("createDate", Operator.GTE, DateTime.parse(param.get("beginDate")+"").toDate()));
		
		if (param.get("endDate") != null)
			filters.add(new SearchFilter("createDate", Operator.LT, DateTime.parse(param.get("endDate")+"").plusDays(1).toDate()));

		Specification<TradeFlowInfoEntity> spec = DynamicSpecifications.bySearchFilter(
				filters, TradeFlowInfoEntity.class);
		return TradeFlowInfoRepository.findAll(spec, pageRequest);
	}

}
