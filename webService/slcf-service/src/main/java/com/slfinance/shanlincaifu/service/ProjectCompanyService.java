/** 
 * @(#)ProjectCompanyService.java 1.0.0 2016年1月11日 上午11:43:05  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 公司服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月11日 上午11:43:05 $ 
 */
public interface ProjectCompanyService {

	/**
	 * 公司列表
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 上午11:44:15
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords:String:记录总数</tt><br>
     *      <tt>data                :String:List<Map<String, Object>></tt><br>
     *      <tt>id                  :String:公司主键</tt><br>
     *      <tt>companyName         :String:公司名称</tt><br>
     *      <tt>projectType         :String:商户类型</tt><br>
     *      <tt>telephone           :String:联系电话</tt><br>
     *      <tt>custName            :String:联系人</tt><br>
     *      <tt>communAddress       :String:联系地址</tt><br>
     *      <tt>memo                :String:备注</tt><br>
	 */
	public ResultVo queryCompanyList(Map<String, Object> params);
	
	/**
	 * 新建公司
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 上午11:44:52
	 * @param params
     *      <tt>companyName  :String:公司名称</tt><br>
     *      <tt>projectType  :String:商户类型</tt><br>
     *      <tt>telephone    :String:联系电话</tt><br>
     *      <tt>custName     :String:联系人</tt><br>
     *      <tt>communAddress:String:联系地址</tt><br>
     *      <tt>memo         :String:备注</tt><br>
     *      <tt>userId       :String:创建人</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "companyName", required = true, requiredMessage = "公司名称不能为空!"),
			@Rule(name = "projectType", required = true, requiredMessage = "商户类型不能为空!"),
			@Rule(name = "telephone", required = true, requiredMessage = "联系电话不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo saveCompany(Map<String, Object> params) throws SLException;
	
	/**
	 * 查看公司
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 上午11:45:56
	 * @param params
     *      <tt>companyId:String:公司id</tt><br>
	 * @return
     *      <tt>id           :String:公司主键</tt><br>
     *      <tt>companyName  :String:公司名称</tt><br>
     *      <tt>projectType  :String:商户类型</tt><br>
     *      <tt>telephone    :String:联系电话</tt><br>
     *      <tt>custName     :String:联系人</tt><br>
     *      <tt>communAddress:String:联系地址</tt><br>
     *      <tt>memo         :String:备注</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "companyId", required = true, requiredMessage = "公司ID不能为空!")
	})
	public ResultVo queryCompanyById(Map<String, Object> params);
	
	/**
	 * 查询所有公司名称
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 下午4:38:55
	 * @param params
	 * @return
     *      <tt>id         :String:公司ID</tt><br>
     *      <tt>companyName:String:公司名称</tt><br>
	 */
	public ResultVo findByCompanyName(Map<String, Object> params);
}
