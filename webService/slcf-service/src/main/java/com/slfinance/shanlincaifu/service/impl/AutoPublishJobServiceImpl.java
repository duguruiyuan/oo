package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AutoPublishInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.repository.AutoPublishInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.custom.AutoPublishRepositoryCustom;
import com.slfinance.shanlincaifu.service.AutoPublishJobService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("autoPublishJobService")
public class AutoPublishJobServiceImpl implements AutoPublishJobService {
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	AutoPublishInfoRepository autoPublishInfoRepository;
	
	@Autowired
	AutoPublishRepositoryCustom autoPublishRepositoryCustom;
	
	@Autowired
	LoanManagerService loanManagerService;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;

	/**
	 * 自动发布
	 * @author guoyk
	 */
	public ResultVo autoPublish(Map<String, Object> params) throws SLException {
		
		List<AutoPublishInfoEntity> autoPublishInfoList = autoPublishInfoRepository.findAllAutoPublishInfo();
		if(autoPublishInfoList.size()!=1){
			return new ResultVo(false,"查询自动发布规则出错");
		}
		Map<String, Object> args = Maps.newHashMap();
		AutoPublishInfoEntity autoPublishInfo = autoPublishInfoList.get(0);
		//资产来源
		String debtSource = autoPublishInfo.getDebtSource();
		//传来的资产来源
	    String[] spArr = debtSource.split(",");
		List<String> split = new ArrayList<String>(Arrays.asList(spArr));
		//情况一：标的个数
		Integer loanNumber = autoPublishInfo.getMaxLoanNumber();
		//按照标的个数自动发布
		if(loanNumber!=0){
			//查询线上募集中的标的个数
			Integer countResult = loanInfoRepository.countLoan();
			//需要自动发标的个数
			Integer resultCount = loanNumber-countResult;
			//需要自动发标的个数>0才继续查找
			if(resultCount>0){
				//定义计数常量,用来计算自动发布的个数
				int flag = 0;
				//加入资产全部循环一遍后，未到达发布的个数，继续循环（发布是按照善林商务、雪澄..排序一个一个发布）
				while (flag<resultCount){
					//对规则中的资产进行循环查找
					for (int j = 0; j < split.size(); j++) {
						//设置sql参数
						args.put("debtSource", split.get(j));
						if(autoPublishInfo.getMinYearRate()!=null&&autoPublishInfo.getMaxYearRate()!=null&&autoPublishInfo.getMaxYearRate()!=BigDecimal.ZERO){
							args.put("minYearRate", autoPublishInfo.getMinYearRate());
							args.put("maxYearRate", autoPublishInfo.getMaxYearRate());
						}
						if(autoPublishInfo.getMinTerm()!=null&&autoPublishInfo.getMaxTerm()!=null && autoPublishInfo.getMaxTerm()!=0){
							args.put("minTerm", autoPublishInfo.getMinTerm());
							args.put("maxTerm", autoPublishInfo.getMaxTerm());
						}
						if(autoPublishInfo.getMinRasieDays()!=null&&autoPublishInfo.getMaxRasieDays()!=null &&autoPublishInfo.getMaxRasieDays()!=0){
							args.put("minRasieDays", autoPublishInfo.getMinRasieDays());
							args.put("maxRasieDays", autoPublishInfo.getMaxRasieDays());
						}
						if(autoPublishInfo.getRepaymentMethod()!=null){
							args.put("repaymentMethod", autoPublishInfo.getRepaymentMethod());
						}
						if(autoPublishInfo.getMinLoanAmount()!=null&&autoPublishInfo.getMaxLoanAmount()!=null&&autoPublishInfo.getMaxLoanAmount()!=BigDecimal.ZERO){
							args.put("minLoanAmount", autoPublishInfo.getMinLoanAmount());
							args.put("maxLoanAmount", autoPublishInfo.getMaxLoanAmount());
						}
						//根据规则进行匹配查找
						List<Map<String, Object>> queryPublishInfo = autoPublishRepositoryCustom.queryPublishInfo(args);
						//当该资产没有一条符合的数据时，从集合中删除该资产，避免下次循环，然后终止此次循环，执行下次
						if(queryPublishInfo==null||queryPublishInfo.size()==0){
							split.remove(j);
							j--;
							continue ;
						}
						//取第一个,自动发布
						for (int k = 0; k < queryPublishInfo.size(); k++) {
							Map<String, Object> resultMap = queryPublishInfo.get(k);
							//排除已参与定时发布的项目
							Date publishDate = (Date)resultMap.get("publishDate");
							Date nowDate = new Date();
							if(publishDate!=null && (publishDate.getTime()-nowDate.getTime())>0){
								continue ;
							}
							//发布
							Map<String, Object> publishMap = Maps.newHashMap();
							publishMap.put("loanId", resultMap.get("id")); 
							publishMap.put("userId", Constant.SYSTEM_USER_BACK); 
							loanManagerService.publishLoanInfo(publishMap);
							//发布一个，计数器加一个
							flag++;
							break;
						}
						if(flag==resultCount){//当计数器等于 需要发布的标的个数 时终止资产循环
							break;
						}
					}
					if(split.size()==0){//当资产集合为空的时候，终止最外层循环
						break;
					}
				}
				//此时循环结束，添加日志
				LogInfoEntity logInfoEntity = new LogInfoEntity();
				logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_PUBLISH_INFO);
				logInfoEntity.setRelatePrimary(autoPublishInfo.getId());
				logInfoEntity.setLogType(Constant.OPERATION_TYPE_68);
				logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_05);
				logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_13);
				logInfoEntity.setOperDesc(String.format("自动发布了%s个", flag));
				logInfoEntity.setOperPerson(Constant.SYSTEM_USER_BACK);
				logInfoEntityRepository.save(logInfoEntity);
			}
		}
		//情况二：按照线上金额自动发布
		BigDecimal totalLoanAmount = autoPublishInfoRepository.findTotalLoanAmount();
		//规则最低线上总金额
		BigDecimal minLoanAmount = autoPublishInfo.getMinTotalLoanAmount();
		BigDecimal maxLoanAmount = autoPublishInfo.getMaxTotalLoanAmount();
		
		//设置布尔值 用来标记 一种情况（当标的个数未设置，而设置了线上最低金额。）
		boolean g = false;
		if(minLoanAmount!=BigDecimal.ZERO && maxLoanAmount!=BigDecimal.ZERO){
			g = true;
		}
		//后台判断
		if((loanNumber==0 && minLoanAmount==BigDecimal.ZERO)||(loanNumber==0 && maxLoanAmount==BigDecimal.ZERO)||(loanNumber==0 && minLoanAmount==BigDecimal.ZERO && maxLoanAmount==BigDecimal.ZERO) ){
			return new ResultVo(false,"线上总金额和标的个数至少填写一项");
		}
		//当 g 为true时 开始循环发布
		if(g){
			//当线上已发布总金额小于 设置的最低金额时 开始循环
			if(minLoanAmount.compareTo(totalLoanAmount)>0){
				//需要发布的金额
				BigDecimal resultAmount = ArithUtil.sub(minLoanAmount, totalLoanAmount);
				//才继续查找
				//定义金额计数常量
				BigDecimal amountflag = BigDecimal.ZERO;
				while (resultAmount.compareTo(amountflag)>0){
					//开启资产循环
					for (int j = 0; j < split.size(); j++) {
						//设置参数
						args.put("debtSource", split.get(j));
						if(autoPublishInfo.getMinYearRate()!=null&&autoPublishInfo.getMaxYearRate()!=null&&autoPublishInfo.getMaxYearRate()!=BigDecimal.ZERO){
							args.put("minYearRate", autoPublishInfo.getMinYearRate());
							args.put("maxYearRate", autoPublishInfo.getMaxYearRate());
						}
						if(autoPublishInfo.getMinTerm()!=null && autoPublishInfo.getMaxTerm()!=null && autoPublishInfo.getMaxTerm()!=0){
							args.put("minTerm", autoPublishInfo.getMinTerm());
							args.put("maxTerm", autoPublishInfo.getMaxTerm());
						}
						if(autoPublishInfo.getMinRasieDays()!=null&&autoPublishInfo.getMaxRasieDays()!=null&&autoPublishInfo.getMaxRasieDays()!=0){
							args.put("minRasieDays", autoPublishInfo.getMinRasieDays());
							args.put("maxRasieDays", autoPublishInfo.getMaxRasieDays());
						}
						if(autoPublishInfo.getRepaymentMethod()!=null){
							args.put("repaymentMethod", autoPublishInfo.getRepaymentMethod());
						}
						if(autoPublishInfo.getMinLoanAmount()!=null&&autoPublishInfo.getMaxLoanAmount()!=null&&autoPublishInfo.getMaxLoanAmount()!=BigDecimal.ZERO){
							args.put("minLoanAmount", autoPublishInfo.getMinLoanAmount());
							args.put("maxLoanAmount", autoPublishInfo.getMaxLoanAmount());
						}
						//当该资产没有一条符合的数据时，从集合中删除该资产，避免下次循环，然后终止此次循环，执行下次
						List<Map<String, Object>> queryPublishInfo = autoPublishRepositoryCustom.queryPublishInfo(args);
						if(queryPublishInfo==null||queryPublishInfo.size()==0){
							split.remove(j);
							j--;
							continue ;
						}
						//取第一个,自动发布
						for (int k = 0; k < queryPublishInfo.size(); k++) {
							Map<String, Object> resultMap = queryPublishInfo.get(k);
							//排除已参与定时发布的项目
							Date publishDate = (Date)resultMap.get("publishDate");
							Date nowDate = new Date();
							if(publishDate!=null && (publishDate.getTime()-nowDate.getTime())>0){
								continue ;
							}
							//发布前先判断
							//发布一个，根据loanId查找金额
							LoanInfoEntity loanInfo = loanInfoRepository.findOne(resultMap.get("id").toString());
							//找到符合标，该标发布的金额
							BigDecimal loanAmount = loanInfo.getLoanAmount();
							//如果发布该标，发标后线上的总金额
							BigDecimal amountNow = ArithUtil.add(totalLoanAmount, loanAmount);
							//当发布该标后线上的总金额大于规则设置的最大金额，终止此次循环，执行下次
							if(amountNow.compareTo(maxLoanAmount)>0 ){
								continue ;
							}
							//发布
							Map<String, Object> publishMap = Maps.newHashMap();
							publishMap.put("loanId", resultMap.get("id")); 
							publishMap.put("userId", Constant.SYSTEM_USER_BACK); 
							loanManagerService.publishLoanInfo(publishMap);
							//发布一个,标记加上
							amountflag = ArithUtil.add(amountflag, loanAmount);
							//发布过后在查一次现在的线上总金额
							totalLoanAmount = autoPublishInfoRepository.findTotalLoanAmount();
							break;
						}
						if(amountflag.compareTo(resultAmount)>=0){
							break;
						}
					}
						if(split.size()==0){
							break;
						}
					}
				//此时循环结束，添加日志
				LogInfoEntity logInfoEntity = new LogInfoEntity();
				logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			 	logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_PUBLISH_INFO);
				logInfoEntity.setRelatePrimary(autoPublishInfo.getId());
				logInfoEntity.setLogType(Constant.OPERATION_TYPE_68);
				logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_05);
				logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_13);
				logInfoEntity.setOperDesc(String.format("自动发布了%s个", amountflag));
				logInfoEntity.setOperPerson(Constant.SYSTEM_USER_BACK);
				logInfoEntityRepository.save(logInfoEntity);
				}
			}
			return new ResultVo(true,"自动发布成功");
		}

	}
