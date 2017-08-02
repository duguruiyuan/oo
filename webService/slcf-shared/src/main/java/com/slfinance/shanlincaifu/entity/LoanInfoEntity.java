package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * BaoTLoanInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_INFO")
public class LoanInfoEntity extends BaseEntity  {

	private static final long serialVersionUID = 3074617426845373528L;
	private String relateType;
	private String productCode;
	private String debtSourceCode;
	private String loanCode;
	private String creditAcctStatus;
	private Long loanTerm;
	private Timestamp grantDate;
	private Timestamp importDate;
	private Timestamp investStartDate;
	private Timestamp investEndDate;
	private String repaymentDay;
	private BigDecimal loanAmount;
	private BigDecimal holdAmount;
	private BigDecimal holdScale=BigDecimal.ZERO;
	private String repaymentMethod;
	private String workType;
	private String loanDesc;
	private String loanTitle;
	private String receiveStatus;
	private String receiveUser;
	@Transient
	private Timestamp firstRepayDay;
	@Transient
	private String debtSourceName;
	
	private Long repaymentCycle;
	private String assetTypeCode;
	
	@ManyToOne(cascade={CascadeType.ALL})           
	@JoinColumn(name="RELATE_PRIMARY")
	@JsonIgnore
	private LoanCustInfoEntity loanCustInfoEntity=new LoanCustInfoEntity();
	
	@OneToOne(optional = true, cascade = CascadeType.ALL, mappedBy = "loanInfoEntity")
	private LoanDetailInfoEntity loanDetailInfoEntity;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="loanInfoEntity",fetch=FetchType.LAZY)
	private List<LoanDetailInfoEntity> loanDetailList=new ArrayList<LoanDetailInfoEntity>();
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="loanEntity",fetch=FetchType.LAZY)
	private List<RepaymentPlanInfoEntity> repaymentPlanList=new ArrayList<RepaymentPlanInfoEntity>();
	
	/**
	 * 暂存、待审核、审核回退、拒绝、待发布、发布中、满标复核、还款中、已到期、已逾期、提前结清、流标
	 */
	private String loanStatus;

	/**
	 * 发布日期
	 */
	private Date publishDate;

	/**
	 * 募集日期
	 */
	private Date rasieEndDate;

	/**
	 * 最小投资金额
	 */
	private BigDecimal investMinAmount;

	/**
	 * 最大投资金额
	 */
	private BigDecimal investMaxAmount;

	/**
	 * 递增金额
	 */
	private BigDecimal increaseAmount;

	/**
	 * 客户ID
	 */
	private String custId;

	/**
	 * 借款类型
	 */
	private String loanType;

	/**
	 * 封闭天数
	 */
	private Integer seatTerm;

	/**
	 * 募集天数
	 */
	private Integer rasieDays;

	/**
	 * 
	 */
	private String loanUnit;

	/**
	 * 折年系数
	 */
	private BigDecimal rebateRatio;

	/**
	 * 公司名称
	 */
	private String companyName;

	/**
	 * 借款申请时间
	 */
	private Date applyTime;

	/**
	 * 车辆品牌
	 */
	private String carType;

	/**
	 * 产权地址
	 */
	private String propertyRight;

	/**
	 * 房屋类型
	 */
	private String houseType;
	
	/**
	 * 放款状态
	 */
	private String grantStatus;
	
	/**
	 * 放款人
	 */
	private String grantUser;
	
	/**
	 * 服务费率
	 */
	private BigDecimal manageRate;

	/**
	 * 平台服务费
	 */
	private BigDecimal platServiceAmount;

	/**
	 * 月账户管理费
	 */
	private BigDecimal monthlyManageAmount;

	/**
	 * 月账户管理费率
	 */
	private BigDecimal monthlyManageRate;

	/**
	 * 提前还款违约金率
	 */
	private BigDecimal advancedRepaymentRate;

	/**
	 * 逾期还款罚息费率
	 */
	private BigDecimal overdueRepaymentRate;

	/**
	 * 商务、企业
	 */
	private BigDecimal awardRate;

	/**
	 * 借款信息
	 */
	private String loanInfo;

	/**
	 * 协议模板
	 */
	private String protocalType;
	
	/**
	 * 资产包方式时录入的最大债权人姓名
	 */
	private String serviceName;

	/**
	 * 资产包方式时录入的最大债权人身份证号
	 */
	private String serviceCode;
	
	/**
	 * 原合同名称
	 */
	private String sourceContractName;

	/**
	 * 原合同编号
	 */
	private String sourceContractCode;

	/**
	 * 债务人
	 */
	private String sourceLoanUser;

	/**
	 * 是否允许自动投标
	 */
	private String isAllowAutoInvest;
	/**
	 * 新手标标识
	 */
	private String newerFlag;

	/**
	 * 服务费扣款方式：线下、期初、期末、期初期末
	 */
	private String manageExpenseDealType;

	/**
	 * 放款方式：
     01：放款即生效
     02：到帐即生效
	 */
	private String grantType;
	
	/**
	 * 附件编辑标识
	 */
	private String attachmentFlag;
	
	/**
	 * 是否跑过智能投顾标识：Y/N，新标发布默认为N
	 */
	private String isRunAutoInvest;
	
	/**
	 * 通知标识 是/否
	 */
	private String pushFlag;

	/**
	 * 是否渠道（是/否）
	 */
	private String channelFlag;

	/**
	 * 特殊用户（是/否）
	 */
	private String specialUsersFlag;

	public LoanDetailInfoEntity getLoanDetailInfoEntity() {
		return loanDetailInfoEntity;
	}

	public void setLoanDetailInfoEntity(LoanDetailInfoEntity loanDetailInfoEntity) {
		this.loanDetailInfoEntity = loanDetailInfoEntity;
	}

	@Column(name = "RELATE_TYPE", length = 200)
	public String getRelateType() {
		return this.relateType;
	}

	public void setRelateType(String relateType) {
		this.relateType = relateType;
	}

	@Column(name = "PRODUCT_CODE", length = 200)
	public String getProductCode() {
		return this.productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Column(name = "DEBT_SOURCE_CODE", length = 200)
	public String getDebtSourceCode() {
		return this.debtSourceCode;
	}

	public void setDebtSourceCode(String debtSourceCode) {
		this.debtSourceCode = debtSourceCode;
	}

	@Column(name = "LOAN_CODE", length = 200)
	public String getLoanCode() {
		return loanCode;
	}

	public void setLoanCode(String loanCode) {
		this.loanCode = loanCode;
	}

	@Column(name = "CREDIT_ACCT_STATUS", length = 200)
	public String getCreditAcctStatus() {
		return this.creditAcctStatus;
	}

	public void setCreditAcctStatus(String creditAcctStatus) {
		this.creditAcctStatus = creditAcctStatus;
	}

	@Column(name = "LOAN_TERM", precision = 10, scale = 0)
	public Long getLoanTerm() {
		return this.loanTerm;
	}

	public void setLoanTerm(Long loanTerm) {
		this.loanTerm = loanTerm;
	}

	@Column(name = "GRANT_DATE", length = 7)
	public Timestamp getGrantDate() {
		//this.grantDate=DateUtils.getTimeStamp(this.firstRepayDay,CommonUtils.emptyToInt(this.repaymentCycle));
		return this.grantDate;
	}

	public void setGrantDate(Timestamp grantDate) {
		this.grantDate = grantDate;
	}

	@Column(name = "IMPORT_DATE", length = 7)
	public Timestamp getImportDate() {
		return this.importDate;
	}

	public void setImportDate(Timestamp importDate) {
		this.importDate = importDate;
	}

	@Column(name = "INVEST_START_DATE", length = 7)
	public Timestamp getInvestStartDate() {	
//		this.investStartDate=DateUtils.getTimeStamp(this.firstRepayDay,CommonUtils.emptyToInt(this.repaymentCycle));
		return this.investStartDate;
	}

	public void setInvestStartDate(Timestamp investStartDate) {
		this.investStartDate = investStartDate;
	}

	@Column(name = "INVEST_END_DATE", length = 7)
	public Timestamp getInvestEndDate() {
		return this.investEndDate;
	}

	public void setInvestEndDate(Timestamp investEndDate) {
		this.investEndDate = investEndDate;
	}

	@Column(name = "REPAYMENT_DAY", length = 200)
	public String getRepaymentDay() {
//		if(this.firstRepayDay!=null){
//			this.repaymentDay=CommonUtils.emptyToString(this.firstRepayDay.getDate());
//		}
		return this.repaymentDay;
	}

	public void setRepaymentDay(String repaymentDay) {
		this.repaymentDay = repaymentDay;
	}

	@Column(name = "LOAN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getLoanAmount() {
		return this.loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}
	
	@Column(name = "HOLD_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getHoldAmount() {
		return holdAmount;
	}

	public void setHoldAmount(BigDecimal holdAmount) {
		this.holdAmount = holdAmount;
	}

	@Column(name = "HOLD_SCALE", precision = 22, scale = 18)
	public BigDecimal getHoldScale() {
		return holdScale;
	}

	public void setHoldScale(BigDecimal holdScale) {
		this.holdScale = holdScale;
	}
	
	@Column(name = "REPAYMENT_METHOD", length = 300)
	public String getRepaymentMethod() {
		return repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	@Column(name = "WORK_TYPE", length = 200)
	public String getWorkType() {
		return this.workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	@Column(name = "LOAN_DESC", length = 2000)
	public String getLoanDesc() {
		return this.loanDesc;
	}

	public Timestamp getFirstRepayDay() {
		return firstRepayDay;
	}

	public void setFirstRepayDay(Timestamp firstRepayDay) {
		this.firstRepayDay = firstRepayDay;
	}

	public void setLoanDesc(String loanDesc) {
		this.loanDesc = loanDesc;
	}

	public LoanCustInfoEntity getLoanCustInfoEntity() {
		return loanCustInfoEntity;
	}

	public void setLoanCustInfoEntity(LoanCustInfoEntity loanCustInfoEntity) {
		this.loanCustInfoEntity = loanCustInfoEntity;
	}

	public List<LoanDetailInfoEntity> getLoanDetailList() {
		return loanDetailList;
	}

	public void setLoanDetailList(List<LoanDetailInfoEntity> loanDetailList) {
		this.loanDetailList = loanDetailList;
	}	
	public List<RepaymentPlanInfoEntity> getRepaymentPlanList() {
		return repaymentPlanList;
	}

	public void setRepaymentPlanList(List<RepaymentPlanInfoEntity> repaymentPlanList) {
		this.repaymentPlanList = repaymentPlanList;
	}

	public String getDebtSourceName() {
		return debtSourceName;
	}

	public void setDebtSourceName(String debtSourceName) {
		this.debtSourceName = debtSourceName;
	}

	@Column(name = "REPAYMENT_CYCLE")
	public Long getRepaymentCycle() {
		return repaymentCycle;
	}

	public void setRepaymentCycle(Long repaymentCycle) {
		this.repaymentCycle = repaymentCycle;
	}

	@Column(name="ASSET_TYPE_CODE")
	public String getAssetTypeCode() {
		return assetTypeCode;
	}

	public void setAssetTypeCode(String assetTypeCode) {
		this.assetTypeCode = assetTypeCode;
	}

	@Column(name = "LOAN_STATUS", length = 50)
	public String getLoanStatus() {
		return this.loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}

	@Column(name = "PUBLISH_DATE", length = 7)
	public Date getPublishDate() {
		return this.publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	@Column(name = "RASIE_END_DATE", length = 7)
	public Date getRasieEndDate() {
		return this.rasieEndDate;
	}

	public void setRasieEndDate(Date rasieEndDate) {
		this.rasieEndDate = rasieEndDate;
	}

	@Column(name = "INVEST_MIN_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestMinAmount() {
		return this.investMinAmount;
	}

	public void setInvestMinAmount(BigDecimal investMinAmount) {
		this.investMinAmount = investMinAmount;
	}

	@Column(name = "INVEST_MAX_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getInvestMaxAmount() {
		return this.investMaxAmount;
	}

	public void setInvestMaxAmount(BigDecimal investMaxAmount) {
		this.investMaxAmount = investMaxAmount;
	}

	@Column(name = "INCREASE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getIncreaseAmount() {
		return this.increaseAmount;
	}

	public void setIncreaseAmount(BigDecimal increaseAmount) {
		this.increaseAmount = increaseAmount;
	}

	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "LOAN_TYPE", length = 50)
	public String getLoanType() {
		return this.loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	@Column(name = "SEAT_TERM", length = 22)
	public Integer getSeatTerm() {
		return this.seatTerm;
	}

	public void setSeatTerm(Integer seatTerm) {
		this.seatTerm = seatTerm;
	}

	@Column(name = "RASIE_DAYS", length = 22)
	public Integer getRasieDays() {
		return this.rasieDays;
	}

	public void setRasieDays(Integer rasieDays) {
		this.rasieDays = rasieDays;
	}

	@Column(name = "LOAN_UNIT", length = 50)
	public String getLoanUnit() {
		return this.loanUnit;
	}

	public void setLoanUnit(String loanUnit) {
		this.loanUnit = loanUnit;
	}

	@Column(name = "REBATE_RATIO", precision = 22, scale = 18)
	public BigDecimal getRebateRatio() {
		return this.rebateRatio;
	}

	public void setRebateRatio(BigDecimal rebateRatio) {
		this.rebateRatio = rebateRatio;
	}

	@Column(name = "COMPANY_NAME", length = 50)
	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Column(name = "APPLY_TIME", length = 7)
	public Date getApplyTime() {
		return this.applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	@Column(name = "CAR_TYPE", length = 50)
	public String getCarType() {
		return this.carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	@Column(name = "PROPERTY_RIGHT", length = 50)
	public String getPropertyRight() {
		return this.propertyRight;
	}

	public void setPropertyRight(String propertyRight) {
		this.propertyRight = propertyRight;
	}

	@Column(name = "HOUSE_TYPE", length = 50)
	public String getHouseType() {
		return this.houseType;
	}

	public void setHouseType(String houseType) {
		this.houseType = houseType;
	}

	@Column(name = "GRANT_STATUS", length = 50)
	public String getGrantStatus() {
		return grantStatus;
	}

	public void setGrantStatus(String grantStatus) {
		this.grantStatus = grantStatus;
	}

	@Column(name = "GRANT_USER", length = 50)
	public String getGrantUser() {
		return grantUser;
	}

	public void setGrantUser(String grantUser) {
		this.grantUser = grantUser;
	}
	
	@Column(name = "MANAGE_RATE", precision = 22, scale = 8)
	public BigDecimal getManageRate() {
		return this.manageRate;
	}

	public void setManageRate(BigDecimal manageRate) {
		this.manageRate = manageRate;
	}
	
	@Column(name = "PLAT_SERVICE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getPlatServiceAmount() {
		return this.platServiceAmount;
	}

	public void setPlatServiceAmount(BigDecimal platServiceAmount) {
		this.platServiceAmount = platServiceAmount;
	}

	@Column(name = "MONTHLY_MANAGE_AMOUNT", precision = 22, scale = 8)
	public BigDecimal getMonthlyManageAmount() {
		return this.monthlyManageAmount;
	}

	public void setMonthlyManageAmount(BigDecimal monthlyManageAmount) {
		this.monthlyManageAmount = monthlyManageAmount;
	}

	@Column(name = "MONTHLY_MANAGE_RATE", precision = 22, scale = 18)
	public BigDecimal getMonthlyManageRate() {
		return this.monthlyManageRate;
	}

	public void setMonthlyManageRate(BigDecimal monthlyManageRate) {
		this.monthlyManageRate = monthlyManageRate;
	}

	@Column(name = "ADVANCED_REPAYMENT_RATE", precision = 22, scale = 18)
	public BigDecimal getAdvancedRepaymentRate() {
		return this.advancedRepaymentRate;
	}

	public void setAdvancedRepaymentRate(BigDecimal advancedRepaymentRate) {
		this.advancedRepaymentRate = advancedRepaymentRate;
	}

	@Column(name = "OVERDUE_REPAYMENT_RATE", precision = 22, scale = 8)
	public BigDecimal getOverdueRepaymentRate() {
		return this.overdueRepaymentRate;
	}

	public void setOverdueRepaymentRate(BigDecimal overdueRepaymentRate) {
		this.overdueRepaymentRate = overdueRepaymentRate;
	}

	@Column(name = "AWARD_RATE", precision = 22, scale = 18)
	public BigDecimal getAwardRate() {
		return this.awardRate;
	}

	public void setAwardRate(BigDecimal awardRate) {
		this.awardRate = awardRate;
	}

	@Column(name = "LOAN_INFO", length = 4000)
	public String getLoanInfo() {
		return this.loanInfo;
	}

	public void setLoanInfo(String loanInfo) {
		this.loanInfo = loanInfo;
	}

	@Column(name = "PROTOCAL_TYPE", length = 50)
	public String getProtocalType() {
		return this.protocalType;
	}

	public void setProtocalType(String protocalType) {
		this.protocalType = protocalType;
	}
	@Column(name = "LOAN_TITLE", length = 500)
	public String getLoanTitle() {
		return loanTitle;
	}
	public void setLoanTitle(String loanTitle) {
		this.loanTitle = loanTitle;
	}
	
	@Column(name = "RECEIVE_STATUS", length = 500)
	public String getReceiveStatus() {
		return receiveStatus;
	}

	public void setReceiveStatus(String receiveStatus) {
		this.receiveStatus = receiveStatus;
	}
	
	@Column(name = "RECEIVE_USER", length = 500)
	public String getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(String receiveUser) {
		this.receiveUser = receiveUser;
	}

	@Column(name = "SERVICE_NAME", length = 500)
	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Column(name = "SERVICE_CODE", length = 50)
	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	
	@Column(name = "SOURCE_CONTRACT_NAME", length = 500)
	public String getSourceContractName() {
		return this.sourceContractName;
	}

	public void setSourceContractName(String sourceContractName) {
		this.sourceContractName = sourceContractName;
	}

	@Column(name = "SOURCE_CONTRACT_CODE", length = 50)
	public String getSourceContractCode() {
		return this.sourceContractCode;
	}

	public void setSourceContractCode(String sourceContractCode) {
		this.sourceContractCode = sourceContractCode;
	}

	@Column(name = "SOURCE_LOAN_USER", length = 500)
	public String getSourceLoanUser() {
		return this.sourceLoanUser;
	}

	public void setSourceLoanUser(String sourceLoanUser) {
		this.sourceLoanUser = sourceLoanUser;
	}
	
	@Column(name = "IS_ALLOW_AUTO_INVEST", length = 50)
	public String getIsAllowAutoInvest() {
		return this.isAllowAutoInvest;
	}

	public void setIsAllowAutoInvest(String isAllowAutoInvest) {
		this.isAllowAutoInvest = isAllowAutoInvest;
	}
	@Column(name = "NEWER_FLAG", length = 50)
	public String getNewerFlag() {
		return this.newerFlag;
	}

	public void setNewerFlag(String newerFlag) {
		this.newerFlag = newerFlag;
	}

	@Column(name = "MANAGE_EXPENSE_DEAL_TYPE", length = 50)
	public String getManageExpenseDealType() {
		return this.manageExpenseDealType;
	}

	public void setManageExpenseDealType(String manageExpenseDealType) {
		this.manageExpenseDealType = manageExpenseDealType;
	}

	@Column(name = "GRANT_TYPE", length = 50)
	public String getGrantType() {
		return this.grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	@Column(name = "ATTACHMENT_FLAG", length = 50)
	public String getAttachmentFlag() {
		return this.attachmentFlag;
	}

	public void setAttachmentFlag(String attachmentFlag) {
		this.attachmentFlag = attachmentFlag;
	}
	
	@Column(name = "IS_RUN_AUTO_INVEST", length = 50)
	public String getIsRunAutoInvest() {
		return this.isRunAutoInvest;
	}

	public void setIsRunAutoInvest(String isRunAutoInvest) {
		this.isRunAutoInvest = isRunAutoInvest;
	}
	
	@Column(name = "PUSH_FLAG", length = 50)
	public String getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(String pushFlag) {
		this.pushFlag = pushFlag;
	}

	@Column(name = "CHANNEL_FLAG", length = 50)
	public String getChannelFlag() {
		return this.channelFlag;
	}

	public void setChannelFlag(String channelFlag) {
		this.channelFlag = channelFlag;
	}

    @Column(name = "SPECIAL_USERS_FLAG", length = 50)
    public String getSpecialUsersFlag() {
        return specialUsersFlag;
    }

    public void setSpecialUsersFlag(String specialUsersFlag) {
        this.specialUsersFlag = specialUsersFlag;
    }
}