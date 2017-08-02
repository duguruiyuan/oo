package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 原线下理财客户信息表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_WEALTH_CUST_INTO")
public class WealthCustIntoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户编号
	 */
	private String customerCode;

	/**
	 * 客户名称
	 */
	private String name;

	/**
	 * 证件类型
	 */
	private String cardType;

	/**
	 * 证件号码
	 */
	private String cardId;

	/**
	 * 客户名称
	 */
	private String customerManagerName;

	/**
	 * 证件类型
	 */
	private String customerManagerCardType;

	/**
	 * 证件号码
	 */
	private String customerManagerCardId;

	/**
	 * 客户经理\理财顾问
	 */
	private String customerManagerId;

	/**
	 * 客户来源
	 */
	private String source;

	/**
	 * 客户类型
潜在客户、储备客户、客户、老客户
	 */
	private Integer type;



	@Column(name = "CUSTOMER_CODE", length = 30)
	public String getCustomerCode() {
		return this.customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	@Column(name = "NAME", length = 150)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CARD_TYPE", length = 2)
	public String getCardType() {
		return this.cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@Column(name = "CARD_ID", length = 50)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "CUSTOMER_MANAGER_NAME", length = 150)
	public String getCustomerManagerName() {
		return this.customerManagerName;
	}

	public void setCustomerManagerName(String customerManagerName) {
		this.customerManagerName = customerManagerName;
	}

	@Column(name = "CUSTOMER_MANAGER_CARD_TYPE", length = 2)
	public String getCustomerManagerCardType() {
		return this.customerManagerCardType;
	}

	public void setCustomerManagerCardType(String customerManagerCardType) {
		this.customerManagerCardType = customerManagerCardType;
	}

	@Column(name = "CUSTOMER_MANAGER_CARD_ID", length = 50)
	public String getCustomerManagerCardId() {
		return this.customerManagerCardId;
	}

	public void setCustomerManagerCardId(String customerManagerCardId) {
		this.customerManagerCardId = customerManagerCardId;
	}

	@Column(name = "CUSTOMER_MANAGER_ID", length = 50)
	public String getCustomerManagerId() {
		return this.customerManagerId;
	}

	public void setCustomerManagerId(String customerManagerId) {
		this.customerManagerId = customerManagerId;
	}

	@Column(name = "SOURCE", length = 50)
	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name = "TYPE", length = 22)
	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
