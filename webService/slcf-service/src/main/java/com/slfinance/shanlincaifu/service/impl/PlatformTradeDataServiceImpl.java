package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustDataEntity;
import com.slfinance.shanlincaifu.entity.PlatformTradeDataEntity;
import com.slfinance.shanlincaifu.repository.CustDataRepository;
import com.slfinance.shanlincaifu.repository.PlatformTradeDataRepository;
import com.slfinance.shanlincaifu.repository.custom.CommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.PlatformTradeDataService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <统计平台数据接口实现类>
 * <功能详细描述>
 * 
 * @author  lzh
 * @version  [版本号, 2017年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service("platformTradeDataService")
public class PlatformTradeDataServiceImpl implements PlatformTradeDataService {

	@Autowired
	private CommissionInfoRepositoryCustom commissionInfoRepositoryCustom;
	@Autowired
	private PlatformTradeDataRepository platformTradeDataRepository;
	@Autowired
	private CustDataRepository custDataRepository;
	
	/**
	 * 每天统计平台数据
	 * @return
	 */
	@Override
	@Transactional
	public ResultVo findTradeDataDay() throws SLException{
		DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate("201707" + "01", "yyyyMMdd"), 1), "yyyyMMdd");
			
			//交易总额、总笔数
			Map<String, Object> tradeTotalAmount = commissionInfoRepositoryCustom.findTradeTotalAmount();
			//累计注册人数
			Map<String, Object> registerTotalNumber = commissionInfoRepositoryCustom.findRegisterTotalNumber();
			//代偿金额
			Map<String, Object> unRepaymentAmount = commissionInfoRepositoryCustom.findUnRepaymentAmount();
			//借款人数
			Map<String, Object> borrowerNumber = commissionInfoRepositoryCustom.findBorrowerNumber();
			//性别比例
			Map<String, Object> genderRatio = commissionInfoRepositoryCustom.findGenderRatio();
			//年龄比例
			Map<String, Object> ageRatio = commissionInfoRepositoryCustom.findAgeRatio();
			//投资人地区分布比例
			Map<String, Object> investerAreaRatio = commissionInfoRepositoryCustom.findInvesterAreaRatio();
			//借款金额分布
			Map<String, Object> borrowAmountRatio = commissionInfoRepositoryCustom.findBorrowAmountRatio();
			//不同期标的数
			Map<String, Object> projectNumberNotTerm = commissionInfoRepositoryCustom.findProjectNumberNotTerm();
			//还款方式
			Map<String, Object> repaymentType = commissionInfoRepositoryCustom.findRepaymentType();
			//借款人地区分布
			Map<String, Object> borrowerAreaRatio = commissionInfoRepositoryCustom.findBorrowerAreaRatio();
		
				//上线时间
				String startDate = Constant.ONLINE_TIME;
				//安全运营天数
				long SafeDays = 0L;
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				try {
					long date = new Date().getTime() - sdf.parse(startDate).getTime();
					SafeDays = date/(24*60*60*1000);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				//查询上月的数据
				PlatformTradeDataEntity platformTradeDataEntity = new PlatformTradeDataEntity();
				List<PlatformTradeDataEntity> oldPlatformDataEntity = platformTradeDataRepository.findLastMonth();
				if (null != oldPlatformDataEntity && !oldPlatformDataEntity.isEmpty()) {
					PlatformTradeDataEntity lastMothData = oldPlatformDataEntity.get(0);
					platformTradeDataEntity.setOverdueAmount(lastMothData.getOverdueAmount());
					platformTradeDataEntity.setTradeAmountMonth(lastMothData.getTradeAmountMonth());
					
				}
				platformTradeDataEntity.setSafeOperation(Integer.valueOf(Long.toString(SafeDays)));
				platformTradeDataEntity.setTradeAmountTotal(new BigDecimal(tradeTotalAmount.get("totalInvestAmount").toString()) );
				platformTradeDataEntity.setTradeTimes(Integer.valueOf(tradeTotalAmount.get("totalTradeVolume").toString()));
				platformTradeDataEntity.setRegisterTotal(Integer.valueOf(registerTotalNumber.get("totalRegister").toString()));
				platformTradeDataEntity.setUnRepaymentAmount(new BigDecimal(unRepaymentAmount.get("compensatoryAmount").toString()));
				platformTradeDataEntity.setBorrowerTotal(Integer.valueOf(borrowerNumber.get("lendNumber").toString()));
				platformTradeDataEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
				platformTradeDataEntity.setCreateDate(new Date());
				platformTradeDataEntity.setCreateUser("root");
				platformTradeDataEntity.setLastUpdateDate(new Date());
				platformTradeDataEntity.setLastUpdateUser("root");
				platformTradeDataEntity.setVersion(1);
				platformTradeDataRepository.save(platformTradeDataEntity);
			
				//查询上个月投资人数据
				List<CustDataEntity> oldInvestData = custDataRepository.findlastMonthData(Constant.FINANCIAL_CLIENTS);
				CustDataEntity investDataEntity = new CustDataEntity();
				if (null != oldInvestData && !oldInvestData.isEmpty()) {
					CustDataEntity lastInvestData = oldInvestData.get(0);
					investDataEntity.setInvestPopulationMonth(lastInvestData.getInvestPopulationMonth());
				}
				investDataEntity.setCustType(Constant.FINANCIAL_CLIENTS);
				//投资人男女比例
				investDataEntity.setGenderProportion(Json.ObjectMapper.writeValue(genderRatio));
				//投资人年龄分布比例
				investDataEntity.setAgeProportion(Json.ObjectMapper.writeValue(ageRatio));
				//投资人区域分布比例
				investDataEntity.setAreaProportion(Json.ObjectMapper.writeValue(investerAreaRatio));
				
				investDataEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
				investDataEntity.setCreateDate(new Date());
				investDataEntity.setCreateUser("root");
				investDataEntity.setLastUpdateDate(new Date());
				investDataEntity.setLastUpdateUser("root");
				investDataEntity.setVersion(1);
				custDataRepository.save(investDataEntity);
			
				//借款人数据
				//Map<String, Object> lendData = (Map<String, Object>) map.get("lendData");
				CustDataEntity lendDataEntity = new CustDataEntity();
				lendDataEntity.setCustType(Constant.CREDIT_CUSTOMERS);
				//借款金额分布比例
				lendDataEntity.setLoanAmountProportion(Json.ObjectMapper.writeValue(borrowAmountRatio));
				//不同期限标的比例
				lendDataEntity.setTimeProportion(Json.ObjectMapper.writeValue(projectNumberNotTerm));
				//还款方式比例
				lendDataEntity.setRepaymentStyleProportion(Json.ObjectMapper.writeValue(repaymentType));
				lendDataEntity.setAreaProportion(Json.ObjectMapper.writeValue(borrowerAreaRatio));
				lendDataEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
				lendDataEntity.setCreateDate(new Date());
				lendDataEntity.setCreateUser("root");
				lendDataEntity.setLastUpdateDate(new Date());
				lendDataEntity.setLastUpdateUser("root");
				custDataRepository.save(lendDataEntity);
			
		
		
		return new ResultVo(true);
	}

	/**
	 * 每月统计平台数据
	 * @return
	 * @throws SLException
	 */
	@Override
	@Transactional
	public ResultVo findTradeDataMonth() throws SLException {
		//逾期金额
		Map<String, Object> overdueAmount = commissionInfoRepositoryCustom.findOverdueAmount();
		//月度成交量
		String tradeAmountMonth = commissionInfoRepositoryCustom.findTradeAmountMonth();
		//投资人数
		String investNumber = commissionInfoRepositoryCustom.findInvestNumber();
		
		List<PlatformTradeDataEntity> list = platformTradeDataRepository.findLastOne();
		PlatformTradeDataEntity	platformTradeDataEntity = list.get(0);
		platformTradeDataEntity.setOverdueAmount(new BigDecimal(overdueAmount.get("OverdueAmount").toString()));
		//月度成交量
		platformTradeDataEntity.setTradeAmountMonth(tradeAmountMonth);
		platformTradeDataEntity.setLastUpdateDate(new Date());
		
		List<CustDataEntity> list1 = custDataRepository.findLastOne(Constant.FINANCIAL_CLIENTS);
		CustDataEntity investDataEntity = list1.get(0);
		//投资人数
		investDataEntity.setInvestPopulationMonth(investNumber);
		investDataEntity.setLastUpdateDate(new Date());
		return new ResultVo(true);
	}

	
}
