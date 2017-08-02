/** 
 * @(#)AccountFlowService.java 1.0.0 2015年4月25日 下午6:17:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;



import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 转盘活动接口
 * @author lixx
 */
public interface TurntableActivityService {
	
	/**
	 *转盘活动列表查询
	 * 
	 * @author lixx
	 * @date 2017-07-07
	 * 
	 * @param params
     *     <tt>start           :String:起始值</tt><br>
     *     <tt>length          :String:长度</tt><br>
	 * @return ResultVo
     *      <tt>data           :String:List<<TurntableActivityEntity>></tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryTurntableActivityList(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 新增和编辑转盘活动
	 * 
	 * @author lixx
	 * @date 2017-07-07
	 * 
	 * @param params
     *     <tt>activityName           :String:活动名称</tt><br>
     *     <tt>startTime              :Date:活动开始时间</tt><br>
     *     <tt>endTime                :Date:活动结束时间</tt><br>
     *     <tt>backgroundImage        :String:背景图片</tt><br>
     *     <tt>pointImage             :String:指针图片</tt><br>
     *     <tt>turntableImage         :String:转盘图片</tt><br>
     *     <tt>activityRule           :String:活动规则</tt><br>
     *     <tt>shareTitle             :String:分享标题</tt><br>
     *     <tt>shareContent           :String:分享内容</tt><br>
     *     <tt>shareImage             :String:分享图片</tt><br>
     *     <tt>participateRule        :String:活动参与规则</tt><br>
     *     <tt>participatePensonnel   :String:活动参与人员</tt><br>
     *     <tt>userId                 :String:操作人</tt><br>
	 * @return ResultVo
     *      
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "activityName", required = true, requiredMessage = "活动名称不能为空!"),
			@Rule(name = "startTime", required = true, requiredMessage = "活动开始时间不能为空!"),
			@Rule(name = "endTime", required = true, requiredMessage = "活动结束时间不能为空!"),
			@Rule(name = "backgroundImage", required = true, requiredMessage = "背景图片不能为空!"),
			@Rule(name = "pointImage", required = true, requiredMessage = "指针图片不能为空!"),
			@Rule(name = "turntableImage", required = true, requiredMessage = "转盘图片不能为空!"),
			@Rule(name = "activityRule", required = true, requiredMessage = "活动规则不能为空!"),
			@Rule(name = "shareTitle", required = true, requiredMessage = "分享标题不能为空!"),
			@Rule(name = "shareContent", required = true, requiredMessage = "分享内容不能为空!"),
			@Rule(name = "shareImage", required = true, requiredMessage = "分享图片不能为空!"),
			@Rule(name = "participateRule", required = true, requiredMessage = "活动参与规则不能为空!"),
			@Rule(name = "participatePensonnel", required = true, requiredMessage = "活动参与人员不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空!"),
			@Rule(name = "backgroundImgName", required = true, requiredMessage = "背景图片名不能为空!"),
			@Rule(name = "pointImgName", required = true, requiredMessage = "指针图片名不能为空!"),
			@Rule(name = "turntableImgName", required = true, requiredMessage = "转盘图片名不能为空!"),
			@Rule(name = "shareImgName", required = true, requiredMessage = "分享图片名不能为空!"),
	})
	public ResultVo saveTurntableActivity(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 新增和编辑转盘信息
	 * 
	 * @author lixx
	 * @date 2017-07-07
	 * 
	 * @param params
     *     <tt>turntableId     :String:转盘活动ID</tt><br>
     *     <tt>userId          :String:操作人</tt><br>
	 * @return ResultVo
     *      
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "turntableId", required = true, requiredMessage = "转盘活动ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空!"),
	})
	public ResultVo saveTurntableInfo(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 查询转盘统计信息
	 * 
	 * @author lixx
	 * @date 2017-07-07
	 * 
	 * @param params
     *     <tt>turntableId      :String:转盘活动ID</tt><br>
     *     
	 * @return ResultVo
     *      
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "turntableId", required = true, requiredMessage = "转盘活动ID不能为空!"),
	})
	public ResultVo queryTurntableSatistics(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 修改转盘活动状态
	 * 
	 * @author lixx
	 * @date 2017-07-07
	 * 
	 * @param params
     *     <tt>turntableId     :String:转盘活动ID</tt><br>
     *     <tt>activityStatus  :String:转盘活动状态：启用：1；禁用：0</tt><br>
     *     <tt>userId          :String:操作人</tt><br>
	 * @return ResultVo
     *      
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "turntableId", required = true, requiredMessage = "转盘活动ID不能为空!"),
			@Rule(name = "activityStatus", required = true, requiredMessage = "转盘活动状态不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空!"),
	})
	public ResultVo updateTurntableActivityStatus(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 查询转盘活动信息
	 * 
	 * @author lixx
	 * @date 2017-07-11
	 * 
	 * @param params
     *     <tt>turntableId     :String:转盘活动ID</tt><br>
	 * @return ResultVo
     *      <tt>data           :Object</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "turntableId", required = true, requiredMessage = "转盘活动ID不能为空!"),
	})
	public ResultVo queryTurntableActivity(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 查询转盘信息
	 * 
	 * @author lixx
	 * @date 2017-07-07
	 * 
	 * @param params
     *     <tt>turntableId      :String:转盘活动ID</tt><br>
	 * @return ResultVo
     *      <tt>data            :String:List<<TurntableInfoEntity>></tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "turntableId", required = true, requiredMessage = "转盘活动ID不能为空!"),
	})
	public ResultVo queryTurntableInfo(Map<String, Object> params) throws SLException;
	
}
