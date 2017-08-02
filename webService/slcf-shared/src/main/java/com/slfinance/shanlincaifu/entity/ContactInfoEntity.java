package com.slfinance.shanlincaifu.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户联系人model
 *  
 * @author zhangzs
 */
@Entity
@Table(name = "BAO_T_CONTACT_INFO")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class ContactInfoEntity extends BaseEntity {

	private static final long serialVersionUID = -1803744265542706090L;
	
	@OneToOne(cascade={CascadeType.ALL},fetch=FetchType.LAZY)           
	@JoinColumn(name="CUST_ID")
	@NotFound(action=NotFoundAction.IGNORE)//找不到用户信息，仍返回
	@JsonIgnore
	private CustInfoEntity custInfo;
	
	@Column(name="CONTACT_NAME",length=150)
	private String contactName;
	
	@Column(name="RELATION_TYPE",length=50)
	private String relationType;
	
	@Column(name="CONTACT_TELEPHONE",length=150)
	private String contanctTelePhone;
	
	@Column(name="DESCR",length=150)
	private String descr;
}