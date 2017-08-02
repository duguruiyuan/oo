package com.slfinance.shanlincaifu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.TurntableActivityEntity;
import com.slfinance.shanlincaifu.entity.TurntableInfoEntity;
import com.slfinance.shanlincaifu.entity.TurntableSatisticsEntity;
import com.slfinance.shanlincaifu.repository.TurntableActivityRepository;
import com.slfinance.shanlincaifu.repository.TurntableInfoRepository;
import com.slfinance.shanlincaifu.repository.TurntableSatisticsRepository;
import com.slfinance.shanlincaifu.service.TurntableActivityService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;


@Service("turntableActivityService")
public class TurntableActivityServiceImpl implements TurntableActivityService {
	
	@Autowired
	private TurntableActivityRepository turntableActivityRepository;
	
	@Autowired
	private TurntableInfoRepository turntableInfoRepository;
	
	@Autowired
	private TurntableSatisticsRepository turntableSatisticsRepository;
	
	
	
	/**
	 *转盘活动列表查询
	 */
	@Override
	public ResultVo queryTurntableActivityList(Map<String, Object> params)
			throws SLException {
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", turntableActivityRepository.findAll(new PageRequest(start, length)).getContent());
		return new ResultVo(true, "查询成功", result);
	}
	
	/**
	 * 新增和编辑转盘活动
	 */
	@Override
	@Transactional
	public ResultVo saveTurntableActivity(Map<String, Object> params)
			throws SLException {
		
		String turntableId = (String) params.get("turntableId");
		String userId = (String) params.get("userId");
		
		String activityName = (String) params.get("activityName");
		Date startTime = DateUtils.parseDate((String)params.get("startTime"), "yyyy-MM-dd HH:mm");
		Date endTime = DateUtils.parseDate((String)params.get("endTime"), "yyyy-MM-dd HH:mm");
		String backgroundImage = (String) params.get("backgroundImage");
		String pointImage = (String) params.get("pointImage");
		String turntableImage = (String) params.get("turntableImage");
		String activityRule = (String) params.get("activityRule");
		String shareTitle = (String) params.get("shareTitle");
		String shareContent = (String) params.get("shareContent");
		String shareImage = (String) params.get("shareImage");
		String participateRule = (String) params.get("participateRule");
		String participatePensonnel = (String) params.get("participatePensonnel");
		String backgroundImgName = (String) params.get("backgroundImgName");
		String pointImgName = (String) params.get("pointImgName");
		String turntableImgName = (String) params.get("turntableImgName");
		String shareImgName = (String) params.get("shareImgName");
		
		TurntableActivityEntity turntableActivityEntity = null;
		if(StringUtils.isEmpty(turntableId)){
			turntableActivityEntity = new TurntableActivityEntity();
			turntableActivityEntity.setBasicModelProperty(userId, true);
			turntableActivityEntity.setTurntableStatus("0");
		}else{
			turntableActivityEntity = turntableActivityRepository.findOne(turntableId);
			if(turntableActivityEntity == null){
				throw new SLException("未找到转盘活动");
			}
			turntableActivityEntity.setBasicModelProperty(userId, false);
		}
		
		turntableActivityEntity.setActivityName(activityName);
		turntableActivityEntity.setStartTime(startTime);
		turntableActivityEntity.setEndTime(endTime);
		turntableActivityEntity.setBackgroundImage(backgroundImage);
		turntableActivityEntity.setPointImage(pointImage);
		turntableActivityEntity.setTurntableImage(turntableImage);
		turntableActivityEntity.setActivityRule(activityRule);
		turntableActivityEntity.setShareTitle(shareTitle);
		turntableActivityEntity.setShareContent(shareContent);
		turntableActivityEntity.setShareImage(shareImage);
		turntableActivityEntity.setParticipateRule(participateRule);
		turntableActivityEntity.setParticipatePensonnel(participatePensonnel);
		turntableActivityEntity.setBackgroundImgName(backgroundImgName);
		turntableActivityEntity.setPointImgName(pointImgName);
		turntableActivityEntity.setTurntableImgName(turntableImgName);
		turntableActivityEntity.setShareImgName(shareImgName);
		turntableActivityRepository.save(turntableActivityEntity);
		
		if(StringUtils.isEmpty(turntableId)){
			//保存转盘统计信息
			TurntableSatisticsEntity turntableSatisticsEntity = new TurntableSatisticsEntity();
			turntableSatisticsEntity.setTurntableId(turntableActivityEntity.getId());
			turntableSatisticsEntity.setParticipateNumber(0);
			turntableSatisticsEntity.setPrizeNumber(0);
			turntableSatisticsEntity.setPrizeProbability(0);
			turntableSatisticsEntity.setShareNumber(0);
			turntableSatisticsEntity.setPageViews(0);
			turntableSatisticsEntity.setVisitorsNumber(0);
			turntableSatisticsEntity.setBasicModelProperty(userId, true);
			turntableSatisticsRepository.save(turntableSatisticsEntity);
		}
		
		return new ResultVo(true, "保存活动成功");
	}
	
	/**
	 * 新增和编辑转盘信息
	 */
	@Override
	@Transactional
	public ResultVo saveTurntableInfo(Map<String, Object> params)
			throws SLException {
		
		String userId = (String) params.get("userId");
		String turntableId = (String) params.get("turntableId");
		@SuppressWarnings("unchecked")
		List<String[]> awardList =  (List<String[]>)params.get("awardList");
		ArrayList<TurntableInfoEntity> turntableInfoEntityList = new ArrayList<>();
		TurntableInfoEntity turntableInfoEntity = null; 
		for(String[] award :  awardList){
			if(StringUtils.isEmpty(award[0])){
				turntableInfoEntity = new TurntableInfoEntity();
			}else{
				turntableInfoEntity = turntableInfoRepository.findOne(award[0]);
				if(turntableInfoEntity == null){
					throw new SLException("未找到转盘信息");
				}
			}
			turntableInfoEntity.setTurntableId(turntableId);
			turntableInfoEntity.setAwardIndex(award[1]);
			turntableInfoEntity.setAwardGrand(award[2]);
			turntableInfoEntity.setAward(award[3]);
			turntableInfoEntity.setAwardImage(award[4]);
			turntableInfoEntity.setAwardAmountEveryday(award[5]);
			turntableInfoEntity.setAwardTotal(award[6]);
			turntableInfoEntity.setAwardProbability(award[7]);
			turntableInfoEntity.setAwardStartTime(award[8]);
			turntableInfoEntity.setAwardEndTime(award[9]);
			turntableInfoEntity.setBasicModelProperty(userId, true);
			turntableInfoEntityList.add(turntableInfoEntity);
		}
		turntableInfoRepository.save(turntableInfoEntityList);
		TurntableActivityEntity turntableActivityEntity = turntableActivityRepository.findOne(turntableId);
		if(turntableActivityEntity == null){
			throw new SLException("未找到转盘活动");
		}
		turntableActivityEntity.setTurntableStatus("1");
		turntableActivityRepository.save(turntableActivityEntity);
		return new ResultVo(true, "转盘保存成功");
	}
	
	
	/**
	 * 查询转盘统计信息
	 */
	@Override
	public ResultVo queryTurntableSatistics(Map<String, Object> params)
			throws SLException {
		String turntableId = (String) params.get("turntableId");
		TurntableSatisticsEntity turntableSatisticsEntity = turntableSatisticsRepository.findByTurntableId(turntableId);
		if(turntableSatisticsEntity != null){
			Map<String, Object> result = Maps.newHashMap();
			result.put("data", turntableSatisticsEntity);
			return new ResultVo(true, "查询成功",result);
		}
		return new ResultVo(false, "查询失败");
	}
	
	/**
	 * 修改转盘活动状态
	 */
	@Override
	public ResultVo updateTurntableActivityStatus(Map<String, Object> params)
			throws SLException {
		
		String userId = (String) params.get("userId");
		String turntableId = (String) params.get("turntableId");
		String activityStatus = (String) params.get("activityStatus");
		TurntableActivityEntity turntableActivityEntity = turntableActivityRepository.findOne(turntableId);
		if(activityStatus.equals("1") || activityStatus.equals("0")){
			turntableActivityEntity.setActivityStatus(activityStatus);
			turntableActivityEntity.setBasicModelProperty(userId, false);
			turntableActivityRepository.save(turntableActivityEntity);
		}else{
			return new ResultVo(false, "转盘活动状态不正确");
		}
		return new ResultVo(true, "状态修改成功");
	}
	
	/**
	 * 查询转盘活动信息
	 */
	@Override
	public ResultVo queryTurntableActivity(Map<String, Object> params)
			throws SLException {
		String turntableId = (String) params.get("turntableId");
		TurntableActivityEntity turntableActivityEntity = turntableActivityRepository.findOne(turntableId);
		if(turntableActivityEntity != null){
			return new ResultVo(true, "查询转盘活动成功", turntableActivityEntity);
		}
		return new ResultVo(false, "未找到转盘活动");
	}
	
	/**
	 * 查询转盘信息
	 */
	@Override
	public ResultVo queryTurntableInfo(Map<String, Object> params)
			throws SLException {
		String turntableId = (String) params.get("turntableId");
		List<TurntableInfoEntity> infoList = turntableInfoRepository.findByTurntableId(turntableId);
		if(infoList != null && !infoList.isEmpty()){
			Map<String, Object> result = Maps.newHashMap();
			result.put("data", infoList);
			return new ResultVo(true, "查询转盘信息成功", result);
		}
		return new ResultVo(false, "未找到转盘信息");
	}

}
