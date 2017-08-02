package com.slfinance.shanlincaifu.entity;
// default package

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * BaoTCustInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BAO_T_CUST_INFO")
public class CustInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -1803744265542706090L;
	private String loginName;
	private String loginPassword;
	private String tradePassword;
	private String credentialsType;
	private String credentialsCode;
	private String custName;
	private String custCode;
	private String birthday;
	private String custLevel;
	private String custGender;
	private String custSource;
	private String custType;
	private String natvicePlaceProvince;
	private String natvicePlaceCity;
	private String natvicePlaceCounty;
	private String natvicePlaceArea;
	private String communAddress;
	private String mobile;
	private String email;
	private String portraitPath;
	private BigDecimal realNameAuthCount=BigDecimal.ZERO;
	private String isLumper;
	private String msgOnOff;
	private String enableStatus;
	
	
	private String inviteCode;
	private Integer integral = 0;
	private String qrCodePath;
	private String inviteOriginId;
	private String queryPermission;
	private String spreadLevel;
	private String loginPwdLevel;
	private String tradePwdLevel;
	
	private String channelSource;
	private String custKind;
	private String tel;
	private String activityPlaceCity;
	
	private String openId;
	
	@Column(name="MEMO2",length=300)
	@Setter
	@Getter
	private String memo2;
	
	@Column(name="MEMO3",length=300)
	@Setter
	@Getter
	private String memo3;
	
	/**邮编**/
	@Column(name="ZIP_CODE",length=50)
	@Setter
	@Getter
	private String zipCode;
	
	/**QQ**/
	@Column(name="QQ_CODE",length=50)
	@Setter
	@Getter
	private String qqCode;	
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="custInfoEntity",fetch=FetchType.LAZY)
	@JsonIgnore
	private List<BankCardInfoEntity> bankCardInfoEntitys = new ArrayList<BankCardInfoEntity>();
	
	@OneToOne(cascade=CascadeType.ALL,mappedBy="custInfo",fetch=FetchType.LAZY)
	@JsonIgnore
	@Setter
	@Getter
	private ContactInfoEntity contanctInfo = new ContactInfoEntity();
	
	
	@Column(name="IS_RECOMMEND",length=50)
	@Setter
	@Getter
	private String isRecommend;

	private String isEmployee;
	
	/**
	 * 线下理财标识
	 * 00:未处理（默认）
	 * 01:已处理
	 */
	@Column(name="WEALTH_FLAG",length=2)
	@Setter
	@Getter
	private String wealthFlag = "00";
	
	/**
	 * 工作状态：在职、离职
	 */
	@Column(name="WORKING_STATE",length=50)
	@Setter
	@Getter
	private String workingState;
	
	/**
	 * 风险评估
	 */
	@Column(name="RISK_ASSESSMENT",length=50)
	@Setter
	@Getter
	private String riskAssessment;
	
	/**
	 * 风险评估答案
	 */
	@Column(name="RISK_ASSESSMENT_ANSWER",length=300)
	@Setter
	@Getter
	private String riskAssessmentAnswer;
	
	/**
	 * 风险评估时间
	 */
	@Column(name="ASSESS_TIME")
	@Setter
	@Getter
	private Date assessTime;

	public List<BankCardInfoEntity> getBankCardInfoEntitys() {
		return bankCardInfoEntitys;
	}

	public void setBankCardInfoEntitys(List<BankCardInfoEntity> bankCardInfoEntitys) {
		this.bankCardInfoEntitys = bankCardInfoEntitys;
	}


	/** default constructor */
	public CustInfoEntity() {
	}

	/** minimal constructor */
	public CustInfoEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public CustInfoEntity(String id, String loginName, 
			String loginPassword, String tradePassword, String credentialsType, 
			String credentialsCode, String custName, String custCode, String birthday, 
			String custLevel, String custGender, String custSource, String custType, 
			String natvicePlaceProvince, String natvicePlaceCity, String natvicePlaceCounty, 
			String natvicePlaceArea, String communAddress, String mobile, String email, 
			String portraitPath, BigDecimal realNameAuthCount, String isLumper, 
			String msgOnOff, String enableStatus, String recordStatus, 
			String createUser, Date createDate, String lastUpdateUser, 
			Date lastUpdateDate, Integer version, String memo) {
		this.id = id;
		this.loginName = loginName;
		this.loginPassword = loginPassword;
		this.tradePassword = tradePassword;
		this.credentialsType = credentialsType;
		this.credentialsCode = credentialsCode;
		this.custName = custName;
		this.custCode = custCode;
		this.birthday = birthday;
		this.custLevel = custLevel;
		this.custGender = custGender;
		this.custSource = custSource;
		this.custType = custType;
		this.natvicePlaceProvince = natvicePlaceProvince;
		this.natvicePlaceCity = natvicePlaceCity;
		this.natvicePlaceCounty = natvicePlaceCounty;
		this.natvicePlaceArea = natvicePlaceArea;
		this.communAddress = communAddress;
		this.mobile = mobile;
		this.email = email;
		this.portraitPath = portraitPath;
		this.realNameAuthCount = realNameAuthCount;
		this.isLumper = isLumper;
		this.msgOnOff = msgOnOff;
		this.enableStatus = enableStatus;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
		this.memo = memo;
	}

	/**
	 * 设置实名认证信息
	 * 
	 * @param custName
	 *            客户姓名
	 * @param credentialsCode
	 *            身份证号码
	 */
	public void setRealNameAuth(String custName, String credentialsCode) {
		this.custName = custName;
		this.credentialsCode = credentialsCode;
		this.birthday = credentialsCode.substring(6, 14);
		this.custGender = Integer.parseInt(credentialsCode.substring(16, 17)) % 2 != 0 ? Constant.SEX_MAN : Constant.SEX_WOMAN;
		this.credentialsType = Constant.CREDENTIALS_ID_CARD;
	}
	
	/**
	 * 设置实名认证信息清空
	 */
	public void setRealNameAuthClear() {
		this.custName = null;
		this.credentialsCode = null;
		this.birthday = null;
		this.custGender = null;
		this.credentialsType = null;
	}

	
	
	
	@Column(name = "LOGIN_NAME", length = 150)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "LOGIN_PASSWORD", length = 50)
	public String getLoginPassword() {
		return this.loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	@Column(name = "TRADE_PASSWORD", length = 50)
	public String getTradePassword() {
		return this.tradePassword;
	}

	public void setTradePassword(String tradePassword) {
		this.tradePassword = tradePassword;
	}

	@Column(name = "CREDENTIALS_TYPE", length = 50)
	public String getCredentialsType() {
		return this.credentialsType;
	}

	public void setCredentialsType(String credentialsType) {
		this.credentialsType = credentialsType;
	}

	@Column(name = "CREDENTIALS_CODE", length = 50)
	public String getCredentialsCode() {
		return this.credentialsCode;
	}

	public void setCredentialsCode(String credentialsCode) {
		this.credentialsCode = credentialsCode;
	}

	@Column(name = "CUST_NAME", length = 150)
	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Column(name = "CUST_CODE", length = 50)
	public String getCustCode() {
		return this.custCode;
	}

	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}

	@Column(name = "BIRTHDAY", length = 8)
	public String getBirthday() {
		return this.birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Column(name = "CUST_LEVEL", length = 50)
	public String getCustLevel() {
		return this.custLevel;
	}

	public void setCustLevel(String custLevel) {
		this.custLevel = custLevel;
	}

	@Column(name = "CUST_GENDER", length = 8)
	public String getCustGender() {
		return this.custGender;
	}

	public void setCustGender(String custGender) {
		this.custGender = custGender;
	}

	@Column(name = "CUST_SOURCE", length = 150)
	public String getCustSource() {
		return this.custSource;
	}

	public void setCustSource(String custSource) {
		this.custSource = custSource;
	}

	@Column(name = "CUST_TYPE", length = 150)
	public String getCustType() {
		return this.custType;
	}

	public void setCustType(String custType) {
		this.custType = custType;
	}

	@Column(name = "NATVICE_PLACE_PROVINCE", length = 150)
	public String getNatvicePlaceProvince() {
		return this.natvicePlaceProvince;
	}

	public void setNatvicePlaceProvince(String natvicePlaceProvince) {
		this.natvicePlaceProvince = natvicePlaceProvince;
	}

	@Column(name = "NATVICE_PLACE_CITY", length = 150)
	public String getNatvicePlaceCity() {
		return this.natvicePlaceCity;
	}

	public void setNatvicePlaceCity(String natvicePlaceCity) {
		this.natvicePlaceCity = natvicePlaceCity;
	}

	@Column(name = "NATVICE_PLACE_COUNTY", length = 150)
	public String getNatvicePlaceCounty() {
		return this.natvicePlaceCounty;
	}

	public void setNatvicePlaceCounty(String natvicePlaceCounty) {
		this.natvicePlaceCounty = natvicePlaceCounty;
	}

	@Column(name = "NATVICE_PLACE_AREA", length = 150)
	public String getNatvicePlaceArea() {
		return this.natvicePlaceArea;
	}

	public void setNatvicePlaceArea(String natvicePlaceArea) {
		this.natvicePlaceArea = natvicePlaceArea;
	}

	@Column(name = "COMMUN_ADDRESS", length = 2000)
	public String getCommunAddress() {
		return this.communAddress;
	}

	public void setCommunAddress(String communAddress) {
		this.communAddress = communAddress;
	}

	@Column(name = "MOBILE", length = 50)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@Column(name = "EMAIL", length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "PORTRAIT_PATH")
	public String getPortraitPath() {
		return this.portraitPath;
	}

	public void setPortraitPath(String portraitPath) {
		this.portraitPath = portraitPath;
	}

	@Column(name = "REAL_NAME_AUTH_COUNT", precision = 22, scale = 0)
	public BigDecimal getRealNameAuthCount() {
		return this.realNameAuthCount;
	}

	public void setRealNameAuthCount(BigDecimal realNameAuthCount) {
		this.realNameAuthCount = realNameAuthCount;
	}

	@Column(name = "IS_LUMPER", length = 50)
	public String getIsLumper() {
		return this.isLumper;
	}

	public void setIsLumper(String isLumper) {
		this.isLumper = isLumper;
	}

	@Column(name = "MSG_ON_OFF", length = 50)
	public String getMsgOnOff() {
		return this.msgOnOff;
	}

	public void setMsgOnOff(String msgOnOff) {
		this.msgOnOff = msgOnOff;
	}

	@Column(name = "ENABLE_STATUS", length = 50)
	public String getEnableStatus() {
		return this.enableStatus;
	}

	public void setEnableStatus(String enableStatus) {
		this.enableStatus = enableStatus;
	}

	@Column(name = "INVITE_CODE", length=50)
	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	@Column(name = "INTEGRAL")
	public Integer getIntegral() {
		return integral;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
	}

	@Column(name = "QR_CODE_PATH",length=1000)
	public String getQrCodePath() {
		return qrCodePath;
	}

	public void setQrCodePath(String qrCodePath) {
		this.qrCodePath = qrCodePath;
	}
	
	@Column(name = "INVITE_ORIGIN_ID",length=50)
	public String getInviteOriginId() {
		return inviteOriginId;
	}

	public void setInviteOriginId(String inviteOriginId) {
		this.inviteOriginId = inviteOriginId;
	}
	
	@Column(name = "QUERY_PERMISSION",length=300)
	public String getQueryPermission() {
		return queryPermission;
	}

	public void setQueryPermission(String queryPermission) {
		this.queryPermission = queryPermission;
	}
	
	@Column(name = "SPREAD_LEVEL",length=4000)
	public String getSpreadLevel() {
		return spreadLevel;
	}

	public void setSpreadLevel(String spreadLevel) {
		this.spreadLevel = spreadLevel;
	}
	
	@Column(name = "LOGIN_PWD_LEVEL",length=50)
	public String getLoginPwdLevel() {
		return loginPwdLevel;
	}

	public void setLoginPwdLevel(String loginPwdLevel) {
		this.loginPwdLevel = loginPwdLevel;
	}
	
	@Column(name = "TRADE_PWD_LEVEL",length=50)
	public String getTradePwdLevel() {
		return tradePwdLevel;
	}

	public void setTradePwdLevel(String tradePwdLevel) {
		this.tradePwdLevel = tradePwdLevel;
	}
	
	@Column(name = "CHANNEL_SOURCE",length=300)
	public String getChannelSource() {
		return channelSource;
	}

	public void setChannelSource(String channelSource) {
		this.channelSource = channelSource;
	}
	
	
	
	
	public boolean update(CustInfoEntity custInfoEntity) {
		//更新实名认证次数
		if (custInfoEntity.getRealNameAuthCount() != null)
			this.setRealNameAuthCount(custInfoEntity.getRealNameAuthCount());
		//更新用户状态
		if (custInfoEntity.getEnableStatus() != null)
			this.setEnableStatus(custInfoEntity.getEnableStatus());
		//更新实用户姓名
		if (custInfoEntity.getCustName() != null)
			this.setCustName(custInfoEntity.getCustName());
		//更新身份证号
		if (custInfoEntity.getCredentialsCode() != null)
			this.setCredentialsCode(custInfoEntity.getCredentialsCode());
		//更新邮件
		if (custInfoEntity.getEmail() != null)
			this.setEmail(custInfoEntity.getEmail());
		//更新生日
		if (custInfoEntity.getBirthday() != null)
			this.setBirthday(custInfoEntity.getBirthday());
		//更新性别
		if (custInfoEntity.getCustGender() != null)
			this.setCustGender(custInfoEntity.getCustGender());
		//更新证件类型
		if (custInfoEntity.getCredentialsType() != null)
			this.setCredentialsType(custInfoEntity.getCredentialsType());
		//更新状态
		if (custInfoEntity.getRecordStatus() != null)
			this.setRecordStatus(custInfoEntity.getRecordStatus());
		//更新最后更新日期
		if (custInfoEntity.getLastUpdateDate() != null)
			this.setLastUpdateDate(custInfoEntity.getLastUpdateDate());
		//更新最后操作人
		if (custInfoEntity.getLastUpdateUser() != null)
			this.setLastUpdateUser(custInfoEntity.getLastUpdateUser());
		return true;
	}

	@Column(name = "IS_EMPLOYEE",length=50)
	public String getIsEmployee() {
		return isEmployee;
	}

	public void setIsEmployee(String isEmployee) {
		this.isEmployee = isEmployee;
	}

	@Column(name = "CUST_KIND",length=150)
	public String getCustKind() {
		return custKind;
	}

	public void setCustKind(String custKind) {
		this.custKind = custKind;
	}

	@Column(name = "TEL",length=150)
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Column(name = "ACTIVITY_PLACE_CITY", length = 50)
	public String getActivityPlaceCity() {
		return this.activityPlaceCity;
	}

	public void setActivityPlaceCity(String activityPlaceCity) {
		this.activityPlaceCity = activityPlaceCity;
	}

    @Column(name = "OPEN_ID", length = 50)
	public String getOpenid() {
		return openId;
	}

	public void setOpenid(String openid) {
		this.openId = openid;
	}
	
}