/** 
 * @(#)APPBankServiceImpl.java 1.0.0 2015年7月20日 下午5:49:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 
package com.slfinance.shanlincaifu.service.mobile.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.mobile.APPBankService;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端银行卡管理业务接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年7月20日 下午5:49:55 $ 
 */
@Service
public class APPBankServiceImpl implements APPBankService {

	@Autowired
	private BankCardService bankCardService;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private  BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom;
	
	/**
	 * 解绑银行卡
	 */
	@Override
	public ResultVo unBindBankCard(Map<String, Object> param)throws SLException {
		return bankCardService.unBindBankCard(param);
	}
	
	
	/***
	 * 根据银行卡ID查询银行的状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getBankCardState( String id )throws SLException{
		String  caseWhenSql = " CASE WHEN B.RECORD_STATUS ='无效' THEN '已解绑' WHEN B.RECORD_STATUS ='有效' AND U.UNBIND_STATUS ='处理中' THEN '解绑审核中'  WHEN B.RECORD_STATUS ='有效' AND U.ID IS NULL  THEN '已绑定'  ELSE '其他'END AS \"state\" ";
		StringBuilder sql = new StringBuilder(" SELECT ").append(caseWhenSql).append(" FROM BAO_T_BANK_CARD_INFO B LEFT JOIN BAO_T_UNBIND_INFO U ON  B.ID = U.RELATE_PRIMARY "); 
		StringBuilder contionSql = new StringBuilder();
		List<Object> paramList=new ArrayList<>(); 
		PageFuns.buildWhereSql(contionSql).append(" B.ID=? ");
		paramList.add(id);
		List<?> stateList = repositoryUtil.queryForMap(sql.append(contionSql).toString(), paramList.toArray());
		if(stateList==null ||(stateList != null && stateList.size() < 1))
			return "";
		return (String)((Map<String,Object>)stateList.get(0)).get("state");
	}
	
	/***
	 * 银行卡列表
	 */
	public ResultVo getBankCardList(Map<String, Object> param) throws SLException{
		param.put("start", 0);
		param.put("length", Integer.MAX_VALUE);
		return new ResultVo(true,"查看银行卡列表成功",bankCardInfoRepositoryCustom.findBankList(param))  ;
	}
	
}
