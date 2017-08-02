package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * 借款协议表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_LOAN_AGREE")
public class LoanAgreeEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 借款人id
	 */
	private String custId;
	
	/**
	 * 银行卡号
	 */
	private String bankNo;

	/**
	 * 银行名
	 */
	private String bankName;
	
	/**
	 * 签约协议号
	 */
	private String noAgree;

	/**
	 * @return the custId
	 */
	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return custId;
	}

	/**
	 * @param custId the custId to set
	 */
	public void setCustId(String custId) {
		this.custId = custId;
	}

	/**
	 * @return the bankNo
	 */
	@Column(name = "BANK_NO", length = 50)
	public String getBankNo() {
		return bankNo;
	}

	/**
	 * @param bankNo the bankNo to set
	 */
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}

	/**
	 * @return the bankName
	 */
	@Column(name = "BANK_NAME", length = 50)
	public String getBankName() {
		return bankName;
	}

	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	/**
	 * @return the noAgree
	 */
	@Column(name = "NO_AGREE", length = 50)
	public String getNoAgree() {
		return noAgree;
	}

	/**
	 * @param noAgree the noAgree to set
	 */
	public void setNoAgree(String noAgree) {
		this.noAgree = noAgree;
	}
	
	
	

}
