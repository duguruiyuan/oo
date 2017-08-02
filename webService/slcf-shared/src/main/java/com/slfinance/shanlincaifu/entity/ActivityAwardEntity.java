package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="BAO_T_ACTIVITY_AWARD")
public class ActivityAwardEntity extends BaseEntity{

	private static final long serialVersionUID = -8644779005170030764L;
	
	@Column(name="AWARD_NAME", length = 50)
	@Setter
	@Getter
	private String awardName;
	
	@Column(name="AWARD_TYPE", length = 50)
	@Setter
	@Getter
	private String awardType;
	
	@Column(name="AWARD_STATUS", length = 32)
	@Setter
	@Getter
	private String awardStatus;
	
	@Column(name="GRANT_AMOUNT",precision = 22, scale = 8)
	@Setter
	@Getter
	private BigDecimal grantAmount;
	
	@Column(name="START_AMOUNT",precision = 22, scale = 8)
	@Setter
	@Getter
	private BigDecimal startAmount;
	
	@Column(name="USE_SCOPE", length = 50)
	@Setter
	@Getter
	private String useScope;
	
	@Column(name="INCREASE_UNIT", length = 50)
	@Setter
	@Getter
	private String increaseUnit;
	
	@Column(name="SEAT_TERM")
	@Setter
	@Getter
	private Integer seatTerm;
	
	@Column(name="LOAN_ALLOTTED_TIME")
	@Setter
	@Getter
	private Integer loanAllottedTime;
	
	@Column(name="START_TIME")
	@Setter
	@Getter
	private Date startTime;
	
	@Column(name="DEADLINE_TIME")
	@Setter
	@Getter
	private Date deadlineTime;
	
	@Column(name="IS_TRANSFER", length = 50)
	@Setter
	@Getter
	private String isTransfer;
	
	@Column(name="SUBJECT_REPAYMENT_METHODS", length = 50)
	@Setter
	@Getter
	private String subjectRepaymentMethods;
	
	/** minimal constructor */
	public ActivityAwardEntity(){
		
	}
	
	/** full constructor */
	public ActivityAwardEntity(String id, String awardName,String awardType,String awardStatus,
			BigDecimal grantAmount,BigDecimal startAmount,String useScope,String increaseUnit,
			Integer seatTerm,Integer loanAllottedTime,Date startTime,Date deadlineTime,String subjectRepaymentMethods,String recordStatus, 
			String isTransfer,String createUser, Date createDate, String lastUpdateUser, 
			Date lastUpdateDate, Integer version, String memo) {
		this.id = id;
		this.awardName = awardName;
		this.awardType = awardType;
		this.awardStatus = awardStatus;
		this.grantAmount = grantAmount;
		this.startAmount = startAmount;
		this.useScope = useScope;
		this.increaseUnit = increaseUnit;
		this.seatTerm = seatTerm;
		this.loanAllottedTime = loanAllottedTime;
		this.startTime = startTime;
		this.deadlineTime = deadlineTime;
		this.subjectRepaymentMethods = subjectRepaymentMethods;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
		this.memo = memo;
	}
}
