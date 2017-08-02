package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ChannelInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.repository.ChannelInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.custom.ChannelManagerRepositoryCustom;
import com.slfinance.shanlincaifu.service.ChannelManagerService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

@Service("channelManagerService")
public class ChannelManagerServiceImpl implements ChannelManagerService {
	@Autowired
	private ChannelInfoRepository channelInfoRepository;
	@Autowired
	private ChannelManagerRepositoryCustom channelManagerRepositoryCustom;
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveChannelInfo(Map<String, Object> params)
			throws SLException {
		String channelNo = (String) params.get("channelNo");
		String custId = params.get("custId").toString();
		BigDecimal channelInfo = channelInfoRepository.getCountByChannelNo(channelNo, "已生效");
		if (channelInfo.compareTo(BigDecimal.ZERO) > 0) {
			return new ResultVo(false, "渠道编号重复!");
		}
		ChannelInfoEntity channelInfoEntity = new ChannelInfoEntity();
		channelInfoEntity.setDeptName((String) params.get("deptName"));
		channelInfoEntity.setDeptManager((String) params.get("deptManager"));
		channelInfoEntity.setChannelName((String) params.get("channelName"));
		channelInfoEntity.setChannelSource((String) params.get("channelSource"));
		channelInfoEntity.setChannelNo((String) params.get("channelNo"));
		channelInfoEntity.setRecordStatus("已生效");
		channelInfoEntity.setBasicModelProperty(custId, true);
		channelInfoRepository.save(channelInfoEntity);
		// 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CHANNEL_INFO);
		logInfoEntity.setRelatePrimary(channelInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_85);
		logInfoEntity.setOperBeforeContent("");
		logInfoEntity.setOperAfterContent(String.format(
				"部门名称=%s，部门负责人=%s，渠道编号=%s，渠道名称=%s，终端=%s，状态=%s",
				channelInfoEntity.getDeptName(),
				channelInfoEntity.getDeptManager(),
				channelInfoEntity.getChannelNo(),
				channelInfoEntity.getChannelName(),
				channelInfoEntity.getChannelSource(),
				channelInfoEntity.getRecordStatus()));
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "渠道管理新建成功");
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo enableRecordStatusInvalid(Map<String, Object> params)
			throws SLException {
		String channelNo = (String) params.get("channelNo");
		String recordStatus = (String) params.get("recordStatus");// 失效
		String custId = params.get("custId").toString();
		ChannelInfoEntity channelInfoEntity = channelInfoRepository.findByChannelNoAndRecordStatus(channelNo, "已生效");
		if (recordStatus.equals("失效") && channelInfoEntity != null) {
			String beforerecordStatus=channelInfoEntity.getRecordStatus();
			channelInfoEntity.setBasicModelProperty(custId, false);
			channelInfoEntity.setRecordStatus(recordStatus);
			channelInfoRepository.save(channelInfoEntity);
			// 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CHANNEL_INFO);
			logInfoEntity.setRelatePrimary(channelInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_86);
			logInfoEntity.setOperBeforeContent(beforerecordStatus);
			logInfoEntity.setOperAfterContent(String.format("状态=%s",
					channelInfoEntity.getRecordStatus()));
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntityRepository.save(logInfoEntity);
		}else{
			return new ResultVo(false, "该渠道未生效,无法设置失效!");
		}
		return new ResultVo(true, "设置失效成功");
	}

	public ResultVo queryChannelInfoList(Map<String, Object> param) {
		Page<Map<String, Object>> pageVo = channelManagerRepositoryCustom.queryChannelInfoList(param);
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "渠道管理列表查询成功", result);
	}

	public ResultVo queryChannelCountList(Map<String, Object> param) {
		Page<Map<String, Object>> pageVo = channelManagerRepositoryCustom.queryChannelCountList(param);
		Map<String, Object> toatlChannelInfo = channelManagerRepositoryCustom.queryToatlChannelInfo(param);
		Map<String, Object> result = Maps.newHashMap();
		result.put("registerCountTotal",toatlChannelInfo != null ? toatlChannelInfo.get("registerCountTotal") == null ? BigDecimal.ZERO: toatlChannelInfo.get("registerCountTotal"): BigDecimal.ZERO);
		result.put("rechargeAmountTotal",toatlChannelInfo != null ? toatlChannelInfo.get("rechargeAmountTotal") == null ? BigDecimal.ZERO: toatlChannelInfo.get("rechargeAmountTotal"): BigDecimal.ZERO);
		result.put("depositAmountTotal",toatlChannelInfo != null ? toatlChannelInfo.get("depositAmountTotal") == null ? BigDecimal.ZERO: toatlChannelInfo.get("depositAmountTotal"): BigDecimal.ZERO);
		result.put("investAmountTotal",toatlChannelInfo != null ? toatlChannelInfo.get("investAmountTotal") == null ? BigDecimal.ZERO: toatlChannelInfo.get("investAmountTotal"): BigDecimal.ZERO);

		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "渠道统计查询成功", result);
	}

	@Override
	public ResultVo queryInvestAmountDetail(Map<String, Object> params) {
		Map<String, Object> result = Maps.newHashMap();
		Page<Map<String, Object>> pageVo = channelManagerRepositoryCustom.queryInvestAmountDetail(params);
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "投资金额明细标查询成功",result);
	}
}
