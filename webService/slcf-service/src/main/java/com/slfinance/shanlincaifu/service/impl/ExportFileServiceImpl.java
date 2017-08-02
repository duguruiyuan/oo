/** 
 * @(#)ExportFileServiceImpl.java 1.0.0 2016年3月7日 下午2:18:18  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ExportFileEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.repository.ExportFileRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.service.ExportFileService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.ProjectService;
import com.slfinance.shanlincaifu.service.WealthInfoService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.MoneyUtil;
import com.slfinance.shanlincaifu.utils.RestClient;
import com.slfinance.shanlincaifu.utils.RestClientProperties;
import com.slfinance.shanlincaifu.utils.ZipUtil;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

/**   
 * 导出文件服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年3月7日 下午2:18:18 $ 
 */
@Slf4j
@Service("exportFileService")
public class ExportFileServiceImpl implements ExportFileService {

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private RestClient client;
	
	@Autowired 
	private RestClientProperties restProp;
	
	@Value(value = "${upload.user.path}")
	private String uploadUserPath;
	
	@Autowired 
	private ExportFileRepository exportFileRepository;
	
	@Autowired
	private WealthInfoService wealthInfoService;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private WealthHoldInfoRepository wealthHoldInfoRepository;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private LoanTransferRepository loanTransferRepository;
	
	private final String generatePDFUrl = "pdf/generatePDF"; 
	
	private final String projectTemplateName = "slcf_project_contract.ftl"; // 企业借款模板
	
	private final String wealthTemplateName = "slcf_wealth_contract.ftl"; // 优选计划模板
	
	private final String loanTemplateName = "slcf_loan_contract.ftl"; // 优选计划债权模板
	
	private final String standTemplateName = "slcf_stand_contract.ftl"; // 优选项目债权模板
	
	private ExportFileService self;//AOP增强后的代理对象  	
	
	@Autowired
	@Qualifier("commonThreadPoolTaskExecutor")
	Executor executor;
	
	@Autowired
	EmailService emailService;
	
	@Override
	public void setSelf(Object proxyBean) {
		self = (ExportFileService) proxyBean;
		System.out.println("ExportFileService = " + AopUtils.isAopProxy(this.self)); 
	}
	
	
	@SuppressWarnings({ "unchecked" })
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo downloadProjectContract(Map<String, Object> params) throws SLException{
		
		Map<String, Object> result = Maps.newHashMap();
		
		String projectId = (String)params.get("projectId");
		String custId = (String)params.get("custId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByCustIdAndRelateTypeAndRelatePrimary(custId, Constant.TABLE_BAO_T_PROJECT_INFO, projectId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		ResultVo resultVo = projectService.queryProjectContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("用户{}下载项目{}失败!失败原因:{}", custId, projectId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载优选计划债权协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("investAmount", ArithUtil.formatNumber(data.get("investAmount")));//NumberFormat.getCurrencyInstance(Locale.getDefault(Locale.Category.FORMAT)).format()
		data.put("totalAmount", ArithUtil.formatNumber(data.get("totalAmount")));//NumberFormat.getCurrencyInstance(Locale.getDefault(Locale.Category.FORMAT)).format(data.get("totalAmount"))
		data.put("effectDate", DateUtils.formatDate(DateUtils.parseDate(data.get("effectDate").toString(), "yyyy-MM-dd"), "yyyy年MM月dd日"));
		data.put("projectEndDate", DateUtils.formatDate(DateUtils.parseDate(data.get("projectEndDate").toString(), "yyyy-MM-dd"), "yyyy年MM月dd日"));		
		
		// 2) 生成文件
		String fileName = "企业借款服务协议.pdf";
		String pathPrefix = String.format("%s/%s/project/%s/", uploadUserPath, custId, projectId);
		ResultVo vo = generatePDF(data, projectTemplateName, pathPrefix, fileName);
		if(!ResultVo.isSuccess(vo)) {
			log.error("用户{}下载项目{}失败!失败原因:{}", custId, projectId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setCustId(custId);
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
		exportFileEntity.setRelatePrimary(projectId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_PROJECT);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(custId, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo downloadWealthContract(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		String wealthId = (String)params.get("wealthId");
		String custId = (String)params.get("custId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByCustIdAndRelateTypeAndRelatePrimary(custId, Constant.TABLE_BAO_T_WEALTH_INFO, wealthId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		ResultVo resultVo = wealthInfoService.queryWealthContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("用户{}下载计划{}失败!失败原因:{}", custId, wealthId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载优选计划债权协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("investAmount", ArithUtil.formatNumber(data.get("investAmount")));
		data.put("investMinAmount", ArithUtil.formatNumber(data.get("investMinAmount")));
		data.put("increaseAmount", ArithUtil.formatNumber(data.get("increaseAmount")));
		data.put("yearRate", ArithUtil.formatPercent((BigDecimal)data.get("yearRate"), 1));
		if(BigDecimal.ZERO.compareTo((BigDecimal)data.get("awardRate")) == 0) {
			data.remove("awardRate");
		}
		else {
			data.put("awardRate", ArithUtil.formatPercent((BigDecimal)data.get("awardRate"), 1));
		}
		data.put("effectDate", DateUtils.formatDate(DateUtils.parseDate(data.get("effectDate").toString(), "yyyy-MM-dd"), "yyyy年MM月dd日"));
		data.put("endDate", DateUtils.formatDate(DateUtils.parseDate(data.get("endDate").toString(), "yyyy-MM-dd"), "yyyy年MM月dd日"));
		data.put("releaseDate", DateUtils.formatDate(DateUtils.parseDate(data.get("releaseDate").toString(), "yyyy-MM-dd"), "yyyy年MM月dd日"));	
				
		// 2) 生成文件
		String fileName = "优选计划协议.pdf";
		String pathPrefix = String.format("%s/%s/wealth/%s/", uploadUserPath, custId, wealthId);
		ResultVo vo = generatePDF(data, wealthTemplateName, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("用户{}下载计划{}失败!失败原因:{}", custId, wealthId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setCustId(custId);
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_WEALTH_INFO);
		exportFileEntity.setRelatePrimary(wealthId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_WEALTH);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(custId, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo downloadWealthLoanContract(Map<String, Object> params) throws SLException{
		Map<String, Object> result = Maps.newHashMap();
		
		String wealthId = (String)params.get("wealthId");
		String custId = (String)params.get("custId");
		String loanId = (String)params.get("loanId");
		
		// 判断债权是否存在
		WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findByCustIdAndLoanIdAndWealthId(custId, loanId, wealthId);
		if(wealthHoldInfoEntity == null) {
			log.error("Step 1.1 未找到用户{}购买优选计划{}持有的债权{}", custId, wealthId, loanId);
			return new ResultVo(false, "未找到债权");
		}
				
		// 1) 取数据
		ResultVo resultVo = wealthInfoService.queryWealthLoanContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("Step 1.2 用户{}下载计划{}的债权{}失败!失败原因:未找到合适的数据", custId, wealthId, loanId);
			return new ResultVo(false, "下载优选计划债权协议失败");
		}
		List<Map<String, Object>> loanList = (List<Map<String, Object>>)resultVo.getValue("data");
		if(loanList == null || loanList.size() == 0) {
			log.error("Step 1.3 用户{}下载计划{}的债权{}失败!失败原因:未找到合适的数据", custId, wealthId, loanId);
			return new ResultVo(false, "下载优选计划债权协议失败");
		}
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByCustIdAndRelateTypeAndRelatePrimary(custId, Constant.TABLE_BAO_T_WEALTH_HOLD_INFO, wealthHoldInfoEntity.getId());
		if(existsExportFile != null && existsExportFile.getFileCounts() == loanList.size()) { // 数据条数跟已导出的文件条数一致则直接下载
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 2) 生成文件
		List<String> urlList = Lists.newArrayList();
		String pathPrefix = String.format("%s/%s/loan/%s/%s/债权转让协议/", uploadUserPath, custId, wealthId, loanId);
		for(int i = 0; i < loanList.size(); i ++) {
			Map<String, Object> data = loanList.get(i);
			data.put("loanAmount", ArithUtil.formatNumber(data.get("loanAmount"))); 
			data.put("loanAmountChinese", MoneyUtil.toChinese(data.get("loanAmount").toString())); 
			data.put("receiverCredentialsCode", Strings.nullToEmpty((String)data.get("receiverCredentialsCode"))); 
			data.put("senderCredentialsCode", Strings.nullToEmpty((String)data.get("senderCredentialsCode"))); 
			data.put("receiverCustName", Strings.nullToEmpty((String)data.get("receiverCustName"))); 
			data.put("senderCustName", Strings.nullToEmpty((String)data.get("senderCustName"))); 
			data.put("receiverLoginName", Strings.nullToEmpty((String)data.get("receiverLoginName"))); 
			data.put("senderLoginName", Strings.nullToEmpty((String)data.get("senderLoginName"))); 
			data.put("receiverAddress", "");// 地址设置为空
			//data.put("receiverAddress", Strings.nullToEmpty((String)data.get("receiverAddress"))); 
			if(StringUtils.isEmpty((String)data.get("investDate"))) {
				data.put("investDate", DateUtils.getCurrentDate("yyyyMMdd"));
			}
			Date investDate = DateUtils.parseDate(Strings.nullToEmpty((String)data.get("investDate")), "yyyyMMdd");
			data.put("currentYear", DateUtils.formatDate(investDate, ("yy")));
			data.put("currentMonth", DateUtils.formatDate(investDate, ("MM")));
			data.put("currentDay", DateUtils.formatDate(investDate, ("dd")));
			
			String fileName = String.format("优选计划债权协议_part%d.pdf", i + 1);
			ResultVo vo = generatePDF(data, loanTemplateName, pathPrefix, fileName);	
			if(!ResultVo.isSuccess(vo)) {
				log.error("Step 1.4 用户{}下载计划{}的债权{}失败!失败原因:{}", custId, wealthId, loanId, (String)vo.getValue("message"));
				return new ResultVo(false, "下载优选计划债权协议失败");
			}	
			urlList.add((String)vo.getValue("message")); // 保存路径
		}
		
		String fileName = "优选计划债权协议.zip";
		String filePath = pathPrefix + fileName;
		if(loanList.size() > 0) { // 债权多余一条则需生成文件并压缩
			// 生成压缩文件
			try {
				File dest = new File(filePath); // 将压缩文件移至指定目录
				if(dest.exists()) {
					dest.delete();
				}
				File file = ZipUtil.zip(pathPrefix);
				if(file == null) {
					log.error("Step 1.6 用户{}下载计划{}的债权{}失败!失败原因:压缩文件失败", custId, wealthId, loanId);
					return new ResultVo(false, "下载优选计划债权协议失败");
				}
				file.renameTo(dest);
				
				// 删除原文件
				for(String path : urlList) {
					File file1 = new File (path);
					file1.delete();
				}
			} catch (IOException e) {
				log.error("压缩文件失败！" + e.getMessage());
				return new ResultVo(false, "下载优选计划债权协议失败");
			}
			
		}

		// 3) 文件入库
		if(existsExportFile != null) {
			existsExportFile.setRecordStatus(Constant.VALID_STATUS_INVALID);
			existsExportFile.setBasicModelProperty(custId, false);
		}
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setCustId(custId);
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_WEALTH_HOLD_INFO);
		exportFileEntity.setRelatePrimary(wealthHoldInfoEntity.getId());
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_LOAN);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath(filePath);
		exportFileEntity.setFileCounts(urlList.size());
		exportFileEntity.setBasicModelProperty(custId, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	}

	/**
	 * 生成PDF文件
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午4:29:16
	 * @param dataSet
	 * @param ftlPath
	 * @param pathPrefix
	 * @param fileName
	 * @return
	 */
	private ResultVo generatePDF(Map<String, Object> dataSet, String ftlPath, String pathPrefix, String fileName) {
		boolean isAsyn = (boolean)dataSet.get("isAsyn");
		
		Map<String, Object> data = Maps.newConcurrentMap();
		data.put("ftlPath", ftlPath);
		data.put("dataSet", dataSet);//Json.ObjectMapper.writeValue(dataSet)
		data.put("pathPrefix", pathPrefix);
		data.put("fileName", fileName);

		ResultVo vo = null;
		
		try {
			vo = client.postForObject(restProp.getFoundtionClient().getServicePrefix() + generatePDFUrl, 
					data, ResultVo.class);
		} catch(ResourceAccessException e){// 如果链接超时，就去延时判断文件存在不存在，
			boolean timeout = e.contains(SocketTimeoutException.class);
			if(timeout){
				String url = pathPrefix + fileName;
				File file = new File(url);
				int maxAttemptTimes = 1;
				int maxCount = isAsyn ? 10 : 3;// 异步多试几次，如果是点击下载就延迟30秒
				do{
					if(file.exists()){
						break;	
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} while(maxAttemptTimes++ < maxCount);
				if(!file.exists()){
					throw e;
				}
				vo = new ResultVo(true, pathPrefix + fileName);
			} else {
				throw e;
			}
		}
		return vo;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo downloadLoanContract(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		String loanId = (String)params.get("loanId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		params.put("isDownload", true);
		ResultVo resultVo = loanManagerService.queryLoanContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载项目协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("isAsyn", (boolean)params.get("isAsyn"));
		// 2) 生成文件
		String fileName = "借款与服务协议.pdf";
		String pathPrefix = String.format("%s/loan/%s/", uploadUserPath, loanId);
		String template = StringUtils.isEmpty((String)data.get("protocolType")) ? standTemplateName : String.format("slcf_stand_contract_%s.ftl", (String)data.get("protocolType"));
		ResultVo vo = generatePDF(data, template, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
		exportFileEntity.setRelatePrimary(loanId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_STAND);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo downloadTransferContract(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		String transferId = (String)params.get("transferId");
		String custId = (String)params.get("custId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_TRANSFER, transferId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		params.put("isDownload", true);
		ResultVo resultVo = loanManagerService.queryTransferContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("下载转让协议{}失败!失败原因:{}", transferId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载转让协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("isAsyn", (boolean)(params.get("isAsyn")==null?false:params.get("isAsyn")));
		// 2) 生成文件
		String fileName = "债权转让协议.pdf";
		String pathPrefix = String.format("%s/loan/%s/%s/", uploadUserPath, custId, transferId);
		
		String modelName = String.format("slcf_transfer_contract_%s.ftl", "old".equals(data.get("oldFlag"))?"z_20170104":(String)data.get("protocolType"));
		String template = StringUtils.isEmpty((String)data.get("protocolType")) ? standTemplateName : modelName;
		ResultVo vo = generatePDF(data, template, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("下载转让协议{}失败!失败原因:{}", transferId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_TRANSFER, transferId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_TRANSFER);
		exportFileEntity.setRelatePrimary(transferId);
		exportFileEntity.setCustId(custId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_TRANSFER);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(custId, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo downloadAssetContract(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String loanId = (String)params.get("loanId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		params.put("isDownload", true);
		ResultVo resultVo = loanManagerService.queryAssetContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载债权转让及回购协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("isAsyn", (boolean)params.get("isAsyn"));
		// 2) 生成文件
		String fileName = "债权转让及回购协议.pdf";
		String pathPrefix = String.format("%s/loan/%s/", uploadUserPath, loanId);
		String template = StringUtils.isEmpty((String)data.get("protocolType")) ? standTemplateName : String.format("slcf_stand_contract_%s.ftl", (String)data.get("protocolType"));
		ResultVo vo = generatePDF(data, template, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("下载计划{}失败!失败原因:{}", loanId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
		exportFileEntity.setRelatePrimary(loanId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_TRANSFER_ATONE);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载债权转让及回购协议成功", result);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo downloadFinancingContract(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String investId = (String) params.get("investId");
		String custId = (String) params.get("custId");
		
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanInfoByInvestId(investId);
		if(loanInfoEntity == null){
			return new ResultVo(false, "借款信息取得出错");
		}
		String loanId = loanInfoEntity.getId();
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_INVEST_INFO, investId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		params.put("isDownload", true);
		ResultVo resultVo = loanManagerService.queryFinancingContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("下载投资{}失败!失败原因:{}", investId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载应收账款转让协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("isAsyn", (boolean)params.get("isAsyn"));
		// 2) 生成文件
		String fileName = "应收账款转让协议.pdf";
		String pathPrefix = String.format("%s/loan/%s/%s/", uploadUserPath, loanId, investId);
		
		String template = StringUtils.isEmpty((String)data.get("protocolType")) ? standTemplateName : String.format("slcf_stand_contract_%s.ftl", (String)data.get("protocolType"));
		ResultVo vo = generatePDF(data, template, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("下载应收账款转让协议{}失败!失败原因:{}", investId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载应收账款转让协议附件失败");
		}
		existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_INVEST_INFO, investId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setCustId(custId);
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		exportFileEntity.setRelatePrimary(investId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_FINANCING);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载应收账款转让协议成功", result);
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo downloadFingertipContract(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String investId = (String) params.get("investId");
		String custId = (String) params.get("custId");
		
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanInfoByInvestId(investId);
		if(loanInfoEntity == null){
			return new ResultVo(false, "借款信息取得出错");
		}
		String loanId = loanInfoEntity.getId();
		
//		String loanId = (String)params.get("loanId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		params.put("isDownload", true);
		ResultVo resultVo = loanManagerService.queryFingertipContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载项目协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("isAsyn", (boolean)params.get("isAsyn"));
		// 2) 生成文件
		String fileName = "借款与服务协议.pdf";
		String pathPrefix = String.format("%s/loan/%s/", uploadUserPath, loanId);
		String template = StringUtils.isEmpty((String)data.get("protocolType")) ? standTemplateName : String.format("slcf_stand_contract_%s.ftl", (String)data.get("protocolType"));
		ResultVo vo = generatePDF(data, template, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
		exportFileEntity.setRelatePrimary(loanId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_STAND);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo downloadContract(Map<String, Object> params)
			throws SLException {
		String investId = (String) params.get("investId");
		String custId = (String) params.get("custId");
		boolean isAsyn = false;// 异步调用时
		if(params.get("isAsyn") != null && (boolean)params.get("isAsyn")){
			isAsyn= true;	
		}
		
		InvestInfoEntity investInfo = investInfoRepository.findOne(investId);
		if(investInfo == null){
			return new ResultVo(false, "投资信息取得出错");
		}
		
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanInfoByInvestId(investId);
		if(loanInfoEntity == null){
			return new ResultVo(false, "借款信息取得出错");
		}
		
		// 产品类型
		String loanType = loanInfoEntity.getLoanType();
		Map<String, Object> subParam = new HashMap<String, Object>();
		subParam.put("isAsyn", isAsyn);
		ResultVo postVo;
		
		if(Constant.INVEST_METHOD_TRANSFER.equals(investInfo.getInvestMode())){
			if(StringUtils.isEmpty(investInfo.getTransferApplyId())){
				return new ResultVo(false, "债权信息取得出错");
			}
			
			WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findByInvestId(investId);
			if(wealthHoldInfoEntity == null) {
				return new ResultVo(false, "缺少持有情况");
			}
			LoanTransferEntity loanTransferEntity = loanTransferRepository.findByReceiveHoldId(wealthHoldInfoEntity.getId());
			if(loanTransferEntity == null) {
				return new ResultVo(false, "缺少转让记录");
			}
			
			// 借款的债权转让
			subParam.put("transferId", loanTransferEntity.getId());
			subParam.put("investId", investId);
			subParam.put("custId", custId);
			postVo = self.downloadTransferContract(subParam);
		} else if(Constant.LOAN_PRODUCT_NAME_01.equals(loanType)){
			// 善转贷 loanId,custId
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.downloadAssetContract(subParam);
		} else if(Constant.LOAN_PRODUCT_NAME_02.equals(loanType)){
			// 善融贷-协议接口
			subParam.put("investId", investId);
			subParam.put("custId", custId);
			postVo = self.downloadFinancingContract(subParam);
		} else if(Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
			// 善意贷-协议接口
			subParam.put("investId", investId);
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.downloadFingertipContract(subParam);
		}  else if(Constant.DEBT_SOURCE_CODE_XCJF.equals(loanInfoEntity.getDebtSourceCode())){
			// 雪澄金服-协议接口
			subParam.put("investId", investId);
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.downloadSnowOrangeContract(subParam);
		}else {
			// 其他借款 类型 loanId
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.downloadLoanContract(subParam);
		}
		return postVo;
	}


	@Override
	public void asynDownloadContract(final Map<String, Object> params)
			throws SLException {
		// 生成协议
		executor.execute(new Runnable() {
			@SuppressWarnings("static-access")
			@Override
			public void run() {
				String loanId = (String) params.get("loanId");
				String investId = (String) params.get("investId");
				String custId = (String) params.get("custId");
				int maxAttemptTimes = 1;
				boolean isSuccess = false;
				Map<String, Object> downLoadParam = Maps.newConcurrentMap();
				do
				{
					try {
						if(!StringUtils.isEmpty(investId)) {
							// 生成协议
							downLoadParam = Maps.newConcurrentMap();
							downLoadParam.put("investId", investId);
							downLoadParam.put("custId", custId);
							downLoadParam.put("isAsyn", true);
							self.downloadContract(downLoadParam);
						}
						else {
							List<InvestInfoEntity> list = investInfoRepository.findByLoanId(loanId);
							for (InvestInfoEntity entity : list) {
								// 生成协议
								downLoadParam = Maps.newConcurrentMap();
								downLoadParam.put("investId", entity.getId());
								downLoadParam.put("custId", entity.getCustId());
								downLoadParam.put("isAsyn", true);
								self.downloadContract(downLoadParam);
							}
						}
						isSuccess = true;
						break;
					} catch (SLException e) {
						log.error("放款时生成协议失败:" + e.getMessage());
						e.printStackTrace();
					} catch (Exception e) {
						log.error("放款时生成协议失败:" + e.getMessage());
						e.printStackTrace();
					}
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} while (maxAttemptTimes ++ < 3);
				if(!isSuccess) {
					//发送通知邮件
					try {
						Map<String, Object> smsInfo = Maps.newHashMap();
						smsInfo.put("to", "wangjingfeng@shanlinjinrong.com");// 收件人邮箱地址
						smsInfo.put("type", MailType.TEXT);
						smsInfo.put("title", "优选项目生成协议失败");
						smsInfo.put("content", String.format("投资ID:%s，生成协议失败", downLoadParam.get("investId").toString()));
						smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
						smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
						emailService.sendEmail(smsInfo);
					} catch (SLException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}


	@Override
	public ResultVo downloadSnowOrangeContract(Map<String, Object> params)throws SLException {

		Map<String, Object> result = Maps.newHashMap();
		
		String loanId = (String)params.get("loanId");
		
		// 判断附件是否存在
		ExportFileEntity existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		
		// 1) 取数据
		params.put("isDownload", true);
		ResultVo resultVo = loanManagerService.querySnowOrangeContract(params);
		if(!ResultVo.isSuccess(resultVo)) { // 判断数据是否取成功
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)resultVo.getValue("message"));
			return new ResultVo(false, "下载项目协议失败");
		}
		
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("isAsyn", (boolean)params.get("isAsyn"));
		// 2) 生成文件
		String fileName = "借款协议.pdf";
		String pathPrefix = String.format("%s/loan/%s/", uploadUserPath, loanId);
		String template = StringUtils.isEmpty((String)data.get("protocolType")) ? standTemplateName : String.format("slcf_stand_contract_%s.ftl", (String)data.get("protocolType"));
		ResultVo vo = generatePDF(data, template, pathPrefix, fileName);	
		if(!ResultVo.isSuccess(vo)) {
			log.error("下载项目{}失败!失败原因:{}", loanId, (String)vo.getValue("message"));
			return new ResultVo(false, "下载项目附件失败");
		}
		existsExportFile = exportFileRepository.findByRelateTypeAndRelatePrimary(Constant.TABLE_BAO_T_LOAN_INFO, loanId);
		if(existsExportFile != null) {
			result.put("url", existsExportFile.getPath());
			return new ResultVo(true, "下载协议成功", result);
		}
		// 3) 文件入库
		ExportFileEntity exportFileEntity = new ExportFileEntity();
		exportFileEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
		exportFileEntity.setRelatePrimary(loanId);
		exportFileEntity.setFileType(Constant.CONTRACT_TYPE_STAND);
		exportFileEntity.setFileName(fileName);
		exportFileEntity.setPath((String)vo.getValue("message"));
		exportFileEntity.setFileCounts(1);
		exportFileEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		exportFileRepository.save(exportFileEntity);
		
		result.put("url", exportFileEntity.getPath());
		return new ResultVo(true, "下载协议成功", result);
	
	}
}
