package com.slfinance.shanlincaifu.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BAO_T_PLATFORM_TRADE_DATA")
public class PlatformTradeDataEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Column(name="SAFE_OPERATION")
	@Setter
	@Getter
	private Integer safeOperation;
	
	@Column(name="TRADE_AMOUNT_TOTAL",precision = 22, scale = 8)
	@Setter
	@Getter
	private BigDecimal tradeAmountTotal;
	
	@Column(name="TRADE_TIMES")
	@Setter
	@Getter
	private Integer tradeTimes;
	
	@Column(name="REGISTER_TOTAL")
	@Setter
	@Getter
	private Integer registerTotal;
	
	@Column(name="OVERDUE_AMOUNT",precision = 22, scale = 8)
	@Setter
	@Getter
	private BigDecimal overdueAmount;
	
	@Column(name="UN_REPAYMENT_AMOUNT",precision = 22, scale = 8)
	@Setter
	@Getter
	private BigDecimal unRepaymentAmount;
	
	@Column(name="BORROWER_TOTAL")
	@Setter
	@Getter
	private Integer borrowerTotal;
	
	@Column(name="TRADE_AMOUNT_MONTH",length = 4000)
	@Setter
	@Getter
	private String tradeAmountMonth;
	
	/** default constructor */
	public PlatformTradeDataEntity() {
	}

	/** minimal constructor */
	public PlatformTradeDataEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}

	/** full constructor */
	public PlatformTradeDataEntity(String id, Integer safeOperation,BigDecimal tradeAmountTotal,
			Integer tradeTimes,Integer registerTotal,BigDecimal overdueAmount,BigDecimal unRepaymentAmount,
			Integer borrowerTotal,String tradeAmountMonth,String recordStatus, 
			String createUser, Date createDate, String lastUpdateUser, 
			Date lastUpdateDate, Integer version, String memo) {
		this.id = id;
		this.safeOperation = safeOperation;
		this.tradeAmountTotal = tradeAmountTotal;
		this.tradeTimes = tradeTimes;
		this.registerTotal = registerTotal;
		this.overdueAmount = overdueAmount;
		this.unRepaymentAmount = unRepaymentAmount;
		this.borrowerTotal = borrowerTotal;
		this.tradeAmountMonth = tradeAmountMonth;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
		this.memo = memo;
	}
}
