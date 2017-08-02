package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;


/**
 * 营业额数据服务类
 * 
 * @author guoyk
 * @date   2017-4-17 
 *
 */
public interface TurnoverService {

	/***
	 * 营业额列表查询
	 * @author  guoyk
	 * @date    2017-4-17 
	 * @param params
     *      <tt>id:String:主键</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryTurnoverList(Map<String, Object> params);
	/***
	 * 营业额汇总查询
	 * @author  guoyk
	 * @date    2017-4-17 
	 * @param params
	 *      <tt>id:String:主键</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryTurnoverInfo(Map<String, Object> params);
	
	/***
	 * 营业额数据保存
	 * @author  guoyk
	 * @date    2017-4-17 
	 * @param params
	 *      <tt>emNO	:String:工号</tt><br>
	 *      <tt>custName:String:姓名</tt><br>
	 *      <tt>custId	:String:客户id</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo saveTurnover(Map<String, Object> params) throws SLException;
	/***
	 * 营业额导入记录查询
	 * @author  guoyk
	 * @date    2017-4-19 
	 * @param params
	 *      <tt>importDate	:String:导入时间</tt><br>
	 *      <tt>importStatus:String:导入状态</tt><br>
	 *      <tt>create_user	:String:创建人员</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo queryImportRecord() throws SLException;
	
}
