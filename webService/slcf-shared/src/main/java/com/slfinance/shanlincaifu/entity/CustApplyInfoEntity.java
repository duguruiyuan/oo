package com.slfinance.shanlincaifu.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * BAO客户申请表 entity. @author Tools
 */
@Entity
@Table(name = "BAO_T_CUST_APPLY_INFO")
public class CustApplyInfoEntity extends BaseEntity  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 客户ID
	 */
	private String custId;

	/**
	 * 金牌推荐人、客户经理变更
	 */
	private String applyType;

	/**
	 * 申请描述
	 */
	private String applyDesc;

	/**
	 * 审核中、通过、拒绝
	 */
	private String applyStatus;

	/**
	 * 申请编号
	 */
	private String applyNo;

	/**
	 * 申请日期
	 */
	private Date applyDate;

	/**
	 * 业务员类型 内部员工、外部客户
	 */
	private String salesmanType;

	/**
	 * 当申请类型为客户经理变更时 有值
	 */
	private String transferCustId;

	/**
	 * 
	 */
	private String newCustManagerId;

	/** default constructor */
	public CustApplyInfoEntity() {
	}

	/** minimal constructor */
	public CustApplyInfoEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public CustApplyInfoEntity(String id, String custId, String applyType, String applyDesc, String applyStatus, String applyNo, Date applyDate, String transferCustId, String recordStatus, String createUser, Date createDate, String lastUpdateUser, Date lastUpdateDate, String memo) {
		this.id = id;
		this.custId = custId;
		this.applyType = applyType;
		this.applyDesc = applyDesc;
		this.applyStatus = applyStatus;
		this.applyNo = applyNo;
		this.applyDate = applyDate;
		this.transferCustId = transferCustId;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.memo = memo;
	}
	
	public CustApplyInfoEntity(String custId, String applyType, String applyDesc, String applyStatus, String applyNo, Date applyDate, String transferCustId) {
		this.custId = custId;
		this.applyType = applyType;
		this.applyDesc = applyDesc;
		this.applyStatus = applyStatus;
		this.applyNo = applyNo;
		this.applyDate = applyDate;
		this.transferCustId = transferCustId;
	}



	@Column(name = "CUST_ID", length = 50)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "APPLY_TYPE", length = 50)
	public String getApplyType() {
		return this.applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	@Column(name = "APPLY_DESC", length = 150)
	public String getApplyDesc() {
		return this.applyDesc;
	}

	public void setApplyDesc(String applyDesc) {
		this.applyDesc = applyDesc;
	}

	@Column(name = "APPLY_STATUS", length = 50)
	public String getApplyStatus() {
		return this.applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}

	@Column(name = "APPLY_NO", length = 50)
	public String getApplyNo() {
		return this.applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	@Column(name = "APPLY_DATE", length = 7)
	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	@Column(name = "SALESMAN_TYPE", length = 50)
	public String getSalesmanType() {
		return this.salesmanType;
	}

	public void setSalesmanType(String salesmanType) {
		this.salesmanType = salesmanType;
	}

	@Column(name = "TRANSFER_CUST_ID", length = 50)
	public String getTransferCustId() {
		return this.transferCustId;
	}

	public void setTransferCustId(String transferCustId) {
		this.transferCustId = transferCustId;
	}

	@Column(name = "NEW_CUST_MANAGER_ID", length = 50)
	public String getNewCustManagerId() {
		return this.newCustManagerId;
	}

	public void setNewCustManagerId(String newCustManagerId) {
		this.newCustManagerId = newCustManagerId;
	}

}
