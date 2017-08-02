package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.commons.collections.MapUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.EmployeeLoadInfoEntity;
import com.slfinance.shanlincaifu.repository.EmployeeLoadInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.EmployeeLoadInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.EmployeeLoadService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import com.slfinance.vo.ResultVo;

@Service("employeeLoadService")
public class EmployeeLoadServiceImpl implements EmployeeLoadService {
	
	@Autowired
	EmployeeLoadInfoRepository employeeLoadInfoRepository;
	
	@Autowired
	EmployeeLoadInfoRepositoryCustom employeeLoadInfoRepositoryCustom;
	
	@Autowired
	@Qualifier("commonThreadPoolTaskExecutor")
	Executor executor;
	
	private EmployeeLoadService self;//AOP增强后的代理对象  
	@Override
	public void setSelf(Object proxyBean) {
		self = (EmployeeLoadService) proxyBean;
		System.out.println("CustomerService=" + AopUtils.isAopProxy(this.self)); 
	}
	
	@Override
	public ResultVo queryAllImportEmployeeLoadInfo(Map<String, Object> params) throws SLException {
		Map<String, Object> data = Maps.newHashMap();
		Page<Map<String, Object>> page = employeeLoadInfoRepositoryCustom.queryAllImportEmployeeLoadInfo(params);
		data.put("iTotalDisplayRecords", page.getTotalElements());
		data.put("data", page.getContent());
		return new ResultVo(true, "查询成功！", data);
	}
	
	@Override
	public ResultVo importEmployeeLoadInfo(Map<String, Object> params) throws SLException {
		ResultVo result = self.handleImportEmployeeLoadInfo(params);
		
		//异步处理数据
		if(ResultVo.isSuccess(result)) {
			executor.execute(new Runnable() {
				public void run() {
					try {
						self.handleOriginalData();
					} catch (SLException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo handleImportEmployeeLoadInfo(Map<String, Object> params) throws SLException {
		String userId = (String) params.get("userId"); //操作人
		Date date = new Date(); //导入时间
		String importBatchNo = (String) params.get("importDate");  //DateUtils.getCurrentDate("yyyyMM"); //SharedUtil.getCurrentTime(); //导入批次号
		if(Strings.isNullOrEmpty(importBatchNo)) {
			return new ResultVo(false, "业绩月份不能为空");
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("list");
		if(null == list || list.size() == 0) {
			return new ResultVo(false, "导入数据不能为空！");
		}
		
		List<Map<String, Object>> errList = Lists.newArrayList(); 		//错误行数据
		List<Map<String, Object>> canImportList = Lists.newArrayList(); //应该插入表数据
		StringBuilder sb = new StringBuilder(""); //错误信息
		valdatorDate(list, errList, canImportList, sb); //校验参数
		
		if(!Strings.isNullOrEmpty(sb.toString())) {
			return new ResultVo(false, sb.toString().substring(0, sb.toString().length() - 1));
		}else {
			List<EmployeeLoadInfoEntity> EmployeeLoadInfoEntityList = Lists.newArrayList();
			for (Map<String, Object> o : canImportList) {
				EmployeeLoadInfoEntity entity = new EmployeeLoadInfoEntity();//new ObjectMapper().convertValue(o, EmployeeLoadInfoEntity.class);
				entity.setBasicModelProperty(userId, true);
				entity.setEmpNo(MapGetStringByKey(o, "empNo"));
				entity.setEmpName(MapGetStringByKey(o, "empName"));
				entity.setJobStatus(MapGetStringByKey(o, "jobStatus"));
				entity.setCredentialsType(MapGetStringByKey(o, "credentialsType"));
				entity.setCredentialsCode(MapGetStringByKey(o, "credentialsCode"));//credentialsCode
				entity.setProvince(MapGetStringByKey(o, "province"));
				entity.setCity(MapGetStringByKey(o, "city"));
				entity.setTeam1(MapGetStringByKey(o, "team1"));
				entity.setTeam4(MapGetStringByKey(o, "team4"));
				entity.setTeam2(MapGetStringByKey(o, "team2"));
				entity.setTeam3(MapGetStringByKey(o, "team3"));
				entity.setJobPosition(MapGetStringByKey(o, "jobPosition"));
				entity.setJobLevel(MapGetStringByKey(o, "jobLevel"));
				entity.setCustManagerName(MapGetStringByKey(o, "custManagerName"));
				entity.setCustManagerCode(MapGetStringByKey(o, "custManagerCode"));
				entity.setCredentialsType(Constant.CREDENTIALS_ID_CARD);
				entity.setStatus(Constant.EMPLOYEE_LOAD_STATUS_01); //状态改成未处理
				entity.setImportBatchNo(importBatchNo); //批次号用当前时间了
				entity.setCreateDate(date);
				EmployeeLoadInfoEntityList.add(entity);
			}
			employeeLoadInfoRepositoryCustom.batchImportDate(EmployeeLoadInfoEntityList, importBatchNo, userId);
		}
		
		return new ResultVo(true, "导入成功 " + canImportList.size() + "比, 失败 " + errList.size() + "比。", errList);
	}
	
	private String MapGetStringByKey(Map<String, Object> o, String key){
		String value = (String) o.get(key);
		return Strings.isNullOrEmpty(value) ? "" : value.trim();
	}
 
	private static int maxLength = 30;
	/**
	 * 校验数据
	 * @author zhiwen_feng
	 * @param list
	 * @param errList
	 * @param canImportList
	 */
	private void valdatorDate(List<Map<String, Object>> list, List<Map<String, Object>> errList, List<Map<String, Object>> canImportList, StringBuilder sb) {
		List<String> idcardList = Lists.newArrayList();
		List<String> empNoList = Lists.newArrayList();
		//校验参数
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> date = list.get(i);
			if(Strings.isNullOrEmpty((String) date.get("empNo")))  //员工编号
				errList.add(errMap(date, "员工编号不能为空！", sb, i));
			else if(MapUtils.getString(date, "empNo").length() > maxLength)
				errList.add(errMap(date, "员工编号长度超长！", sb, i));
			else if(empNoList.contains(date.get("empNo"))) 
				errList.add(errMap(date, "员工编号重复！", sb, i));
			
			else if (Strings.isNullOrEmpty((String) date.get("empName")))  //员工姓名
				errList.add(errMap(date, "姓名不能为空！", sb, i));
			else if (MapUtils.getString(date, "empName").length() > maxLength)
				errList.add(errMap(date, "姓名长度超长！", sb, i));
			
			else if (Strings.isNullOrEmpty((String) date.get("credentialsCode"))) //身份证号
				errList.add(errMap(date, "证件号码 不能为空！", sb, i));
			else if (!RuleUtils.isIdentification((String) date.get("credentialsCode"))) //身份证格式
				errList.add(errMap(date, "证件号码格式错误！", sb, i));
			else if(idcardList.contains(date.get("credentialsCode"))) 
				errList.add(errMap(date, "身份证号码重复！", sb, i));
			
			else if (!Strings.isNullOrEmpty((String) date.get("jobStatus")) && MapUtils.getString(date, "jobStatus").length() > maxLength) { //状态
				errList.add(errMap(date, "状态长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("province")) && MapUtils.getString(date, "province").length() > maxLength) { //省份
				errList.add(errMap(date, "省份长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("city")) && MapUtils.getString(date, "city").length() > maxLength) { //城市
				errList.add(errMap(date, "城市长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("team1")) && MapUtils.getString(date, "team1").length() > maxLength) {//部门
				errList.add(errMap(date, "部门长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("team4")) && MapUtils.getString(date, "team4").length() > maxLength) { //所在团队
				errList.add(errMap(date, "所在团队长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("team2")) && MapUtils.getString(date, "team2").length() > maxLength) { //所在门店
				errList.add(errMap(date, "所在门店长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("team3")) && MapUtils.getString(date, "team3").length() > maxLength) { //所在大团队
				errList.add(errMap(date, "所在大团队长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("jobPosition")) && MapUtils.getString(date, "jobPosition").length() > maxLength) { //职位
				errList.add(errMap(date, "职位长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("jobLevel")) && MapUtils.getString(date, "jobLevel").length() > maxLength) { //职级
				errList.add(errMap(date, "职级长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("custManagerName")) && MapUtils.getString(date, "custManagerName").length() > maxLength) { //直属领导姓名
				errList.add(errMap(date, "直属领导姓名长度超长！", sb, i));
			}
			else if (!Strings.isNullOrEmpty((String) date.get("custManagerCode")) && MapUtils.getString(date, "custManagerCode").length() > maxLength) { //直属领导证件号码
				errList.add(errMap(date, "直属领导证件号码长度超长！", sb, i));
			}
			else {
				idcardList.add((String) date.get("credentialsCode"));
				empNoList.add((String) date.get("empNo"));
				canImportList.add(date);
			}
		}
		
	}
	
	/**
	 * 错误信息
	 * @param map
	 * @param errMessage
	 * @return
	 */
	private Map<String, Object> errMap(Map<String, Object> map, String errMessage, StringBuilder sb, int i) {
		Map<String, Object> errMap = Maps.newHashMap();
		errMap.put("empNo", map.get("empNumber"));
		errMap.put("errMessage", errMessage);
		
		sb.append("第" + i + 2 + "行， " + errMessage + "|" );
		return errMap;
	}
	

	@Override
	public ResultVo queryAllEmployeeLoadInfo(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		Page<Map<String, Object>> page = employeeLoadInfoRepositoryCustom.queryAllEmployeeLoadInfo(params);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return new ResultVo(true, "查询成功", result);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo handleOriginalData() throws SLException {
		return employeeLoadInfoRepositoryCustom.handleOriginalData();
	}

}
