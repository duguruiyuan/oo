/** 
 * @(#)FinancialStatisticsService.java 1.0.0 2015年8月11日 下午6:15:18  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.FinancialStatisticsCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.office.ExcelConstants;
import com.slfinance.shanlincaifu.utils.office.ExcelUtil;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

/**   
 * 财务统计接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月11日 下午6:15:18 $ 
 */
@Service
@Transactional(readOnly=true)
@Slf4j
public class FinancialStatisticsService {
	
	@Autowired
	FinancialStatisticsCustom financialStatisticsCustom;
	
	@Autowired
	EmailService emailService;
	
	//文件excel存储地址
	@Value("${upload.repayment.path}")
	protected String repaymentUploadPath;
	
	//文件名称
	private String repaymentFileName = "还款记录列表";
	
	//应还款人员邮件list
	@Value("${repaFinancialEmail}")
	private String repaFinancialEmail;
	
	public ResultVo sendRepaymentEmail() throws SLException{
		if(repaFinancialEmail == null)
			return new ResultVo(false,"邮箱内容为空");
		String[] repaFinancialEmailArray = repaFinancialEmail.split(",");
		if(repaFinancialEmailArray == null || ( repaFinancialEmailArray != null && repaFinancialEmailArray.length < 1 ) )
			return new ResultVo(false,"邮箱内容为空");
		return emailService.sendBatchEmail(assembleRepaymentData(repaFinancialEmailArray));
	}
	
//------私有方法区--------------------------------------------------------------------------------------------------------------
	private List<Map<String, Object>> assembleRepaymentData(String[] repaFinancialEmailArray) throws SLException {
		List<Map<String, Object>> paramsMapList = new ArrayList<Map<String,Object>>();
		Map<String, Object> paramsMap = Maps.newHashMap();
		try {
			List<Map<String, Object>> repaymentList = financialStatisticsCustom.getRepaymentList();
			if(repaymentList != null && repaymentList.size() > 0){
				String fileLoad = ExcelUtil.createExcel(repaymentList,repaymentUploadPath, repaymentFileName,ExcelConstants.REPAYMENT_TITLE_COLUMINDEX2KEY_MAP,ExcelConstants.REPAYMENT_COLUMINDEX2KEY_MAP);
				File f = new File(repaymentUploadPath + fileLoad);
				byte[] cbuf = new byte[(int) f.length()];
				FileInputStream fr = new FileInputStream(f);
				fr.read(cbuf);
				fr.close();
				paramsMap.put("attrPath", new String(org.apache.commons.codec.binary.Base64.encodeBase64(cbuf)));
				paramsMap.put("type", MailType.ATTACHMENT);
				paramsMap.put("attrPathName", "还款计划列表.xlsx");
				paramsMap.put("content", "应还款数据列表，请参看附件<<还款计划列表.xls>>。");
			}else{
				paramsMap.put("type", MailType.TEXT);
				paramsMap.put("content", "当天没有应还款数据。");
			}
			paramsMap.put("title", "还款计划列表");
		} catch (Exception e) {
			log.info(e.getMessage(),e);
			throw new SLException();
		}
		
		for( String email : repaFinancialEmailArray  ) {
			Map<String, Object> emailMap = Maps.newHashMap();
			emailMap.put("fromNickName",Constant.FORM_PLAT_NAME);
			emailMap.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
			emailMap.put("attrPath", paramsMap.get("attrPath"));
			emailMap.put("type", paramsMap.get("type"));
			emailMap.put("attrPathName", paramsMap.get("attrPathName"));
			emailMap.put("content", paramsMap.get("content"));
			emailMap.put("title", paramsMap.get("title"));
			emailMap.put("to", email);
			paramsMapList.add(emailMap);
		}
		return paramsMapList;
	}
	
}
