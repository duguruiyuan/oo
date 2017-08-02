/**
 * 
 */
package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 基础实体模型
 * 
 * @author liubin
 *
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntity extends AbstractEntity {

	@Column(length = 150)
	protected String lastUpdateUser;

	/** 更新时间 */
	@Temporal(TemporalType.TIMESTAMP)
	protected Date lastUpdateDate = new Date();
	/** 版本 */
	@Version
	protected Integer version = 0;

	@Column
	protected String recordStatus="有效";
	
	/**
	 * @author HuangXiaodong 2015-04-17
	 * 设置记录基础信息
	 * 
	 * @param custId
	 *            操作人
	 * @param isInsert
	 *            是否插入
	 */
	public void setBasicModelProperty(String custId, boolean isInsert) {
		if (isInsert) {
			this.setCreateUser(custId);
			this.setCreateDate(new Date());
		}
		this.setLastUpdateUser(custId);
		this.setLastUpdateDate(new Date());
	}

}
