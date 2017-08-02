package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.BusinessImportInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.repository.BusinessDeptInfoRepository;
import com.slfinance.shanlincaifu.repository.BusinessImportInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.custom.BusinessDeptRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.TurnoverInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.TurnoverService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;
@Service("turnoverService")
public class TurnoverServiceImpl implements TurnoverService {

	@Autowired
	public TurnoverInfoRepositoryCustom turnoverInfoRepositoryCustom;
	@Autowired
	public BusinessDeptInfoRepository businessDeptInfoRepository;
	@Autowired
	public LogInfoEntityRepository logInfoEntityRepository;
	@Autowired
	public BusinessImportInfoRepository businessImportInfoRepository;
	@Autowired
	public BusinessDeptRepositoryCustom businessDeptRepositoryCustom;
	
	/***
	 * 营业额列表查询
	 * @author  guoyk
	 * @date    2017-4-17 
	 * @param params
     *      <tt>id:String:主键</tt><br>
     * @return ResultVo
	 */
	public ResultVo queryTurnoverList(Map<String, Object> params) {
		Page<Map<String, Object>> pageVo = turnoverInfoRepositoryCustom.queryTurnoverInfoList(params);
		BigDecimal investTotalAmount = turnoverInfoRepositoryCustom.queryTurnoverTotalAmount(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("investCount", pageVo.getTotalElements());
		result.put("investTotalAmount", investTotalAmount!=null?investTotalAmount:BigDecimal.ZERO);
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "营业额列表查询成功", result);
	}

	/***
	 * 营业额汇总查询
	 * @author  guoyk
	 * @date    2017-4-17 
	 * @param params
	 *      <tt>id:String:主键</tt><br>
	 * @return ResultVo
	 */
	public ResultVo queryTurnoverInfo(Map<String, Object> params) {
		
		Page<Map<String, Object>> pageVo = turnoverInfoRepositoryCustom.queryTurnoverList(params);
		BigDecimal investTotalAmount = turnoverInfoRepositoryCustom.queryTurnoverTotalAmountList(params);
		Map<String, Object> result = Maps.newHashMap();
		result.put("investCount", pageVo.getTotalElements());
		result.put("investTotalAmount", investTotalAmount!=null?investTotalAmount:BigDecimal.ZERO);
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "营业额列表查询成功", result);
	}

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
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_business", allEntries = true, cacheManager="redis") })
	public ResultVo saveTurnover(Map<String, Object> params) throws SLException{
		
		if (params.size()==0||StringUtils.isEmpty(params)) {
			throw new SLException("营业额数据为空");
		}
		//先生成import表
		String userId = (String)params.get("userId");
		//记录导入人员等
		Date now = new Date();
		BusinessImportInfoEntity businessImportInfo = new BusinessImportInfoEntity();
		businessImportInfo.setBasicModelProperty(userId, true);
		businessImportInfo.setImportDate(now);
		businessImportInfo.setImportStatus("");
		String nowString = String.valueOf(DateUtils.getMonth(now));
		Integer nowInteger = Integer.valueOf(nowString)+1;
		businessImportInfo.setImportMonth(String.valueOf(nowInteger));
		//获取新生成的bussinessImportId
		BusinessImportInfoEntity newBusinessImportInfo = businessImportInfoRepository.save(businessImportInfo);
		String bussinessImportId = newBusinessImportInfo.getId();
		
		//假如有一条未导入进去，更新导入记录表，记录失败，否则更新成功
		BusinessImportInfoEntity resutBusinessImportInfo = businessImportInfoRepository.findOne(bussinessImportId);
		
		List<Map<String, Object>> param = (List<Map<String, Object>>)params.get("bigList");
		for (Map<String, Object> map : param) {
			map.put("bussinessImportId", bussinessImportId);
		}
		// 1)批量插入营业部组织结构数据 并删除重复数据
		int batchInsertBusinessDept = businessDeptRepositoryCustom.batchInsertBusinessDept(param);
		if (batchInsertBusinessDept==0) {
			resutBusinessImportInfo.setImportStatus("失败");
			return new ResultVo(false,"导入失败");
		}
	
		// 2)批量更新营业部组织结构
		Map<String,Object> mapsMap = Maps.newHashMap();
		mapsMap.put("userId", userId);
		mapsMap.put("bussinessImportId", bussinessImportId);
		Integer affectedRows = turnoverInfoRepositoryCustom.updateTurnover(mapsMap);
		if (affectedRows==0) {
			resutBusinessImportInfo.setImportStatus("失败");
			return new ResultVo(false,"更新数据失败");
		}
		//否则就成功
		resutBusinessImportInfo.setImportStatus("成功");
		businessImportInfoRepository.save(resutBusinessImportInfo);
		// 3)插入日志
		//记录日志表
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_BUSINESS_DEPT_INFO);
		logInfoEntity.setRelatePrimary("");
		logInfoEntity.setLogType("");
		logInfoEntity.setCreateDate(new Date());
		logInfoEntity.setOperBeforeContent("");
		logInfoEntity.setOperAfterContent(String.format(""));
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(userId);
		logInfoEntityRepository.save(logInfoEntity);
	
		
		return new ResultVo(true,"营业额数据保存成功");
	}

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
	public ResultVo queryImportRecord() throws SLException {
		List<Map<String,Object>> list = turnoverInfoRepositoryCustom.queryImportRecord();
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", list);
		return new ResultVo(true, "营业额导入记录查询成功", result);
	}

}
