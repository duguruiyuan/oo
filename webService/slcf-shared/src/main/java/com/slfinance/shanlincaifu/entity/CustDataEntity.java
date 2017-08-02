package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BAO_T_CUST_DATA")
public class CustDataEntity extends BaseEntity{

	private static final long serialVersionUID = 1L;
	@Column(name="CUST_TYPE",length=50)
	@Setter
	@Getter
	private String custType;
	
	@Column(name="INVEST_POPULATION_MONTH",length = 4000)
	@Setter
	@Getter
	private String investPopulationMonth;
	
	@Column(name="GENDER_PROPORTION",length=50)
	@Setter
	@Getter
	private String genderProportion;
	
	@Column(name="AGE_PROPORTION",length=300)
	@Setter
	@Getter
	private String ageProportion;
	
	@Column(name="AREA_PROPORTION",length=2000)
	@Setter
	@Getter
	private String areaProportion;
	
	@Column(name="LOAN_AMOUNT_PROPORTION",length=300)
	@Setter
	@Getter
	private String loanAmountProportion;
	
	@Column(name="TIME_PROPORTION",length=300)
	@Setter
	@Getter
	private String timeProportion;
	
	@Column(name="REPAYMENT_STYLE_PROPORTION",length=300)
	@Setter
	@Getter
	private String repaymentStyleProportion;
	
	/** default constructor */
	public CustDataEntity() {
	}

	/** minimal constructor */
	public CustDataEntity(String id, Date createDate) {
		this.id = id;
		this.createDate = createDate;
	}
	/** full constructor */
	public CustDataEntity(String id,String custType, String investPopulationMonth, String genderProportion,
			String ageProportion, String areaProportion, String loanAmountProportion, 
			String timeProportion, String repaymentStyleProportion, String recordStatus,
			String createUser, Date createDate, String lastUpdateUser, 
			Date lastUpdateDate, Integer version, String memo) {
		this.id = id;
		this.custType = custType;
		this.investPopulationMonth = investPopulationMonth;
		this.genderProportion = genderProportion;
		this.ageProportion = ageProportion;
		this.areaProportion = areaProportion;
		this.loanAmountProportion = loanAmountProportion;
		this.timeProportion = timeProportion;
		this.repaymentStyleProportion = repaymentStyleProportion;
		this.recordStatus = recordStatus;
		this.createUser = createUser;
		this.createDate = createDate;
		this.lastUpdateUser = lastUpdateUser;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
		this.memo = memo;
		
	}
}
