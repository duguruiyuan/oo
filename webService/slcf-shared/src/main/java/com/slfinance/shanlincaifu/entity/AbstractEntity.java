package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;


@SuppressWarnings("serial")
@MappedSuperclass
@Data
@ToString(callSuper = false)
@EqualsAndHashCode
public abstract class AbstractEntity implements java.io.Serializable{

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(length = 50)
	protected String id;

	/** 创建时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createDate=new Date();

	/** 创建人 */
	@Column(length = 150)
	protected String createUser;

	/** 备注 */
	@Column(length = 300)
	protected String memo;
}
