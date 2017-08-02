package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 花名册原始数据服务类
 * 
 * @author zhiwen_feng
 *
 */
public interface EmployeeLoadService extends BeanSelfAware {
	
	/**
	 * 查询所有导入列表 
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
	public ResultVo queryAllImportEmployeeLoadInfo(Map<String, Object> params) throws SLException;

	/**
	 * 导入原始数据
	 * 
	 * @author zhiwen_feng
	 * @date 2016-04-14
	 * @param Map<String, Object>
	 * 		<tt>userId          :String:操作人</tt><br>
	 * 		list:List<Map<String, Object>>
     *      <tt>empNo          :String:工号</tt><br>
     *      <tt>empName        :String:姓名</tt><br>
     *      <tt>jobStatus      :String:状态</tt><br>
     *      <tt>credentialsType:String:证件类型</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>province       :String:省份</tt><br>
     *      <tt>city           :String:城市</tt><br>
     *      <tt>team1          :String:部门</tt><br>
     *      <tt>team4          :String:所在团队</tt><br>
     *      <tt>team2          :String:所在门店</tt><br>
     *      <tt>team3          :String:所在大团队</tt><br>
     *      <tt>jobPosition    :String:职位</tt><br>
     *      <tt>jobLevel       :String:职级</tt><br>
     *      <tt>custManagerName:String:直属领导姓名</tt><br>
     *      <tt>custManagerCode:String:直属领导证件号码</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo importEmployeeLoadInfo(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询所有导入原始数据列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-04-14
	 * @param params
	 *      <tt>start		   :int:分页起始页</tt><br>
     *      <tt>length		   :int:每页长度</tt><br>
     *      <tt>empName        :String:姓名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>province       :String:省份</tt><br>
     *      <tt>city           :String:省份</tt><br>
     *      <tt>term4          :String:所在团队</tt><br>
     *      <tt>term2          :String:所在门店/营业部	</tt><br>
	 * @return ResultVo
	 * 	    date : Map<String, Object>:
	 *      iTotalDisplayRecords:总条数 
	 * 	    date : List<Map<String, Object>>
     *      <tt>empNo          :String:工号</tt><br>
     *      <tt>empName        :String:姓名</tt><br>
     *      <tt>jobStatus      :String:状态</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>province       :String:省份</tt><br>
     *      <tt>city           :String:省份</tt><br>
     *      <tt>term1          :String:部门</tt><br>
     *      <tt>term4          :String:所在团队</tt><br>
     *      <tt>term3          :String:所在大团队</tt><br>
     *      <tt>term2          :String:所在门店/营业部</tt><br>
     *      <tt>jobPosition    :String:职位</tt><br>
     *      <tt>jobLevel       :String:职级</tt><br>
     *      <tt>custManageName :String:直属领导姓名</tt><br>
     *      <tt>custManageCode :String:直属领导证件号码</tt><br>
     *      <tt>id             :String:主键id</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
	public ResultVo queryAllEmployeeLoadInfo(Map<String, Object> params)throws SLException;
	
	/**
	 * 处理原始数据
	 * 
	 * @author zhiwen_feng
	 * @dare 	2016-04-18
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo handleOriginalData()throws SLException;
	
	public ResultVo handleImportEmployeeLoadInfo(Map<String, Object> params) throws SLException;
	
	
}
