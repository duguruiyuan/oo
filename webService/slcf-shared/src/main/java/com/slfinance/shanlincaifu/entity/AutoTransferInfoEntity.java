package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.math.BigDecimal;


/**
 * 用户自动转让表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_AUTO_TRANSFER_INFO")
public class AutoTransferInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户信息主键ID
	 */
	private String custId;

	/**
	 * 开启时间
	 */
	private Date openDate;

	/**
	 * 开启状态
	 */
	private String openStatus;

	/**
	 * 优先级别
	 */
	private String custPriority;

	/**
	 * 最低期限
	 */
	private Integer limitedTerm;

	/**
	 * 回款方式,等额本息,每期还息到期付本,到期还本付息，多个用逗号隔开
	 */
	private String repaymentMethod;

	/**
	 * 可投产品：优选项目、债权转让，多个用逗号隔开
	 */
	private String canTransferProduct;

	/**
	 * 最低利率
	 */
	private BigDecimal minYearRate;

	/**
	 * 最高利率
	 */
	private BigDecimal maxYearRate;

	/**
	 * 最低期限
	 */
	private Integer minTerm;

	/**
	 * 最高期限
	 */
	private Integer maxTerm;

	/**
	 * 提示状态：Y/N
	 */
	private String pointStatus;



	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "OPEN_DATE", length = 7)
	public Date getOpenDate() {
		return this.openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	@Column(name = "OPEN_STATUS", length = 50)
	public String getOpenStatus() {
		return this.openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	@Column(name = "CUST_PRIORITY", length = 50)
	public String getCustPriority() {
		return this.custPriority;
	}

	public void setCustPriority(String custPriority) {
		this.custPriority = custPriority;
	}

	@Column(name = "LIMITED_TERM", length = 22)
	public Integer getLimitedTerm() {
		return this.limitedTerm;
	}

	public void setLimitedTerm(Integer limitedTerm) {
		this.limitedTerm = limitedTerm;
	}

	@Column(name = "REPAYMENT_METHOD", length = 500)
	public String getRepaymentMethod() {
		return this.repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

	@Column(name = "CAN_TRANSFER_PRODUCT", length = 500)
	public String getCanTransferProduct() {
		return this.canTransferProduct;
	}

	public void setCanTransferProduct(String canTransferProduct) {
		this.canTransferProduct = canTransferProduct;
	}

	@Column(name = "MIN_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getMinYearRate() {
		return this.minYearRate;
	}

	public void setMinYearRate(BigDecimal minYearRate) {
		this.minYearRate = minYearRate;
	}

	@Column(name = "MAX_YEAR_RATE", precision = 22, scale = 18)
	public BigDecimal getMaxYearRate() {
		return this.maxYearRate;
	}

	public void setMaxYearRate(BigDecimal maxYearRate) {
		this.maxYearRate = maxYearRate;
	}

	@Column(name = "MIN_TERM", length = 22)
	public Integer getMinTerm() {
		return this.minTerm;
	}

	public void setMinTerm(Integer minTerm) {
		this.minTerm = minTerm;
	}

	@Column(name = "MAX_TERM", length = 22)
	public Integer getMaxTerm() {
		return this.maxTerm;
	}

	public void setMaxTerm(Integer maxTerm) {
		this.maxTerm = maxTerm;
	}

	@Column(name = "POINT_STATUS", length = 50)
	public String getPointStatus() {
		return this.pointStatus;
	}

	public void setPointStatus(String pointStatus) {
		this.pointStatus = pointStatus;
	}

}
