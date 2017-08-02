package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 线下服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年7月11日 上午15:42:01 $ 
 */
public interface OfflineWealthService {

	
	/**
	 * 线下客户转移
	 * @author zhiwen_feng
	 * @serialData
	 * @return
	 */
	public ResultVo transferOffLineCustManagerjob() throws SLException;

	/**
	 * 增量同步线下理财的客户信息和投资信息到财富中
	 * @author liyy
	 * @serialData
	 * @return
	 */
	public ResultVo synchronizeCustInfoAndInvestInfoFromWealthToSlcf();	
	
	
	/**
	 * 客户转移
	 * 
	 * @param custId
	 * @param credentialsCode
	 * @return
	 * @throws SLException
	 */
	public ResultVo custTransfer(String custId, String credentialsCode) throws SLException;
	
	/**
	 * 历史投资 (线下理财)
	 * 
	 * @param params
     *      <tt>start           :String:起始值 </tt><br>
     *      <tt>length       	:String:长度 </tt><br>
     *      <tt>custId       	:String:客户ID </tt><br>
     *      <tt>beginInvestDate :String: 投资开始时间（可选）</tt><br>
     *      <tt>endInvestDate	:String:投资结束时间（可选） </tt><br>
     *      <tt>investStatus 	:String:投资状态（可选） </tt><br>
	 * @return ResultVo 
     *      <tt>记录总数        	:String: iTotalDisplayRecords</tt><br>
     *      <tt>data        :String:List<Map<String, Object>> </tt><br>
     *      <tt>投资主键        	:String: investId</tt><br>
     *      <tt>lendingNo   :String:出借编号 </tt><br>
     *      <tt>contractNo  :String:合同编号 </tt><br>
     *      <tt>typeTerm    :String:产品期限(月) </tt><br>
     *      <tt>yearRate    :String:年化收益率 </tt><br>
     *      <tt>investAmount:String:投资金额 </tt><br>
     *      <tt>investDate  :String:投资开始日期 </tt><br>
     *      <tt>expireDate  :String:预计到期日期 </tt><br>
     *      <tt>atoneDate   :String:实际结束日期 </tt><br>
     *      <tt>investStatus:String:投资状态 </tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryOfflineInvestList(Map<String, Object> params) throws SLException;
	
}
