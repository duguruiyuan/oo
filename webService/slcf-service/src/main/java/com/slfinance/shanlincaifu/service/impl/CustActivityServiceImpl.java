package com.slfinance.shanlincaifu.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.ActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.repository.ActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustActivityService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("custActivityService")
public class CustActivityServiceImpl implements CustActivityService {

	@Autowired
	private CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;
	
	@Autowired
	private ActivityInfoRepository activityInfoRepository;
	
	@Autowired
	RedisTemplate<Object, Object> redisTemplate;
	
	@Autowired
	private ParamService paramService;
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryAwardBankCardList(Map<String, Object> params) {
		
		Map<String, Object> result = (Map<String, Object>)redisTemplate.opsForValue().get("slcf:query_award_bankcard_list" + ":" + params.get("start") + ":"  + params.get("length"));
		if(result == null) {
			result = Maps.newHashMap();
			ActivityInfoEntity activityInfoEntity = activityInfoRepository.findOne(Constant.ACTIVITY_ID_REGIST_09);
			if(activityInfoEntity == null) {
				return new ResultVo(false, "活动不存在");
			}
			Map<String, Object> requestParam = Maps.newHashMap();
			requestParam.put("startDate", activityInfoEntity.getStartDate());
			requestParam.put("expireDate", activityInfoEntity.getExpireDate());
			requestParam.put("start", params.get("start"));
			requestParam.put("length", params.get("length"));
			requestParam.put("excludeCustIds", paramService.findExcludeCustIds());
			Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.findCustByBindCard(requestParam);
			List<Map<String, Object>> list = page.getContent();
			for(int i = 0; i < list.size(); i ++) {
				int level = Integer.parseInt(list.get(i).get("ROWNUM_").toString());
				String awardName = "";
				if(level == 1) { // 第1名
					awardName = "iPhone7plus";
				}
				else if(level == 2) {// 第2名
					awardName = "iPad Pro 32G";
				}
				else if(level == 3) {
					awardName = "2000中石化油卡";
				}
				else if(level >= 4 && level <= 6) {
					awardName = "1500京东卡";
				}
				else if(level >= 7 && level <= 10) {
					awardName = "1000京东卡";
				}
				else if(level >= 11 && level <= 20) {
					awardName = "588现金奖励";
				}
				else if(level >= 21 && level <= 30) {
					awardName = "388现金奖励";
				}
				else if(level >= 31 && level <= 40) {
					awardName = "288现金奖励";
				}
				else if(level >= 41 && level <= 50) {
					awardName = "188现金奖励";
				}
				else if(level >= 51 && level <= 60) {
					awardName = "88现金奖励";
				}
				else {
					awardName = "无";
				}
				list.get(i).put("awardName", awardName);
			}
			result.put("iTotalDisplayRecords", page.getTotalElements());
			result.put("data", list);
			
			redisTemplate.opsForValue().set("slcf:query_award_bankcard_list" + ":" + params.get("start") + ":"  + params.get("length"), result, 5, TimeUnit.MINUTES);
		}

		return new ResultVo(true, "查询奖励成功", result);
	}

	@Override
	public ResultVo queryAwardList(Map<String, Object> params) {
		
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findOne(Constant.ACTIVITY_ID_REGIST_11);
		if(activityInfoEntity == null) {
			return new ResultVo(false, "活动不存在");
		}
		Map<String, Object> requestParam = Maps.newHashMap();
		requestParam.put("startDate", activityInfoEntity.getStartDate());
		requestParam.put("expireDate", activityInfoEntity.getExpireDate());
		requestParam.put("start", params.get("start"));
		requestParam.put("length", params.get("length"));
		
		Map<String, Object> result = Maps.newHashMap();
		Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.queryAwardList(requestParam);
		List<Map<String, Object>> list = page.getContent();
		for (int i = 0; i < list.size(); i++) {
			int level = Integer.parseInt(list.get(i).get("ROWNUM_").toString());
			String awardAmount = "";
			if (level <= 10) { // 1到10名
				awardAmount = "5888元";
			} else if (level >= 11 && level <= 20) {
				awardAmount = "2888元";
			} else if (level >= 21 && level <= 80) {
				awardAmount = "1188元";
			} else if (level >= 81 && level <= 120) {
				awardAmount = "558元";
			} else {
				awardAmount = "无";
			}
			list.get(i).put("awardAmount", awardAmount);
			BigDecimal yearAchievement=  (BigDecimal) list.get(i).get("yearAchievement");
			list.get(i).put("yearAchievement",yearAchievement);
		}
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", list);
		return new ResultVo(true, "查询红包奖励成功", result);
	}

	@Override
	public ResultVo redEnvelopeList(Map<String, Object> params) {
		//查询红包之前把过期的置为已过期
		custActivityInfoRepositoryCustom.updateActivityByExpireDate();
		ImmutableMap<String, Object> requestParams = ImmutableMap.of("start", CommonUtils.emptyToInt(params.get("start")),
				"length", CommonUtils.emptyToInt(params.get("length")),
				"custId", params.get("custId"),
				"tradeStatus", CommonUtils.emptyToString(params.get("tradeStatus")));

		Object data =  custActivityInfoRepositoryCustom.redEnvelopeList(requestParams);

		return new ResultVo(true, "查询我的红包列表成功", data);
	}

	@Override
	public Map<String, Object> findRewardByIdAndCustId(String custActivityId, String custId) {
		return custActivityInfoRepositoryCustom.findRewardByIdAndCustId(custActivityId, custId);
	}

	@Override
	public CustActivityInfoEntity findById(String custActivityId) {
		return custActivityInfoRepositoryCustom.findById(custActivityId);
	}

//	public ResultVo queryActualTimeAwardList(Map<String, Object> params) {
//		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findOne(Constant.ACTIVITY_ID_REGIST_18);
//		if (activityInfoEntity == null) {
//			return new ResultVo(false, "活动不存在");
//		}
//		Map<String, Object> requestParam = Maps.newHashMap();
//		requestParam.put("startDate", activityInfoEntity.getStartDate());
//		requestParam.put("expireDate", activityInfoEntity.getExpireDate());
//		requestParam.put("start", params.get("start"));
//		requestParam.put("length", params.get("length"));
//
//		Map<String, Object> result = Maps.newHashMap();
//		Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.queryActualTimeAwardList(requestParam);
//		List<Map<String, Object>> list = page.getContent();
//		for (int i = 0; i < list.size(); i++) {
//			int level = Integer.parseInt(list.get(i).get("ROWNUM_").toString());
//			String currentAward = "";
//			// 1到10名
//			if (level == 1) {
//				currentAward = "周生生100克Au999.9黄金金片";
//			} else if (level == 2) {
//				currentAward = "iphone7plus 128g";
//			} else if (level == 3) {
//				currentAward = "ipad pro 32g";
//			} else if (level == 4) {
//				currentAward = "价值2000元中石化油卡";
//			} else if (level >= 5 && level <= 10) {
//				currentAward = "价值1000元京东卡";
//			} else {
//				currentAward = "无";
//			}
//			list.get(i).put("currentAward", currentAward);
//		}
//		result.put("iTotalDisplayRecords", page.getTotalElements());
//		result.put("data", list);
//		return new ResultVo(true, "查询实时豪礼榜", result);
//
//	}
}  

