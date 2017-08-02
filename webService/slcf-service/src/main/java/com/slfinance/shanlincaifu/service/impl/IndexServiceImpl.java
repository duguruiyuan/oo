package com.slfinance.shanlincaifu.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.service.IndexService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/** 
 * @author gaoll
 * @version 创建时间：2015年11月10日 上午11:08:37 
 */
@Service("indexService")
public class IndexServiceImpl implements IndexService {
	
	@Autowired
	private FixedInvestRepositoryCustom fixedInvestRepositoryCustom;

	/**
	 * 查询首页产品信息
	 */
	@Override
	public ResultVo queryProduct() throws SLException {
		List<Map<String,Object>> data = Lists.newArrayList();
		Map<String,Object> paramsMapSec = Maps.newHashMap();
		paramsMapSec.put("pageNum", 0);
		paramsMapSec.put("pageSize", Integer.MAX_VALUE);
		paramsMapSec.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_01, Constant.PRODUCT_TYPE_04, Constant.PRODUCT_TYPE_03));
		Page<Map<String,Object>> secPage = fixedInvestRepositoryCustom.getFixedProductsListPage(paramsMapSec);
		if(secPage != null )
			data.addAll(secPage.getContent());
		return new ResultVo(true,"产品列表查询成功",data);
	}

}
