/*
 * 文 件 名:  SpecialUsersUrlEntity.java
 * 描    述:  <描述>
 * 修 改 人:  xp_Huang
 * 修改时间:  2017年7月21日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.slfinance.shanlincaifu.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * <特殊用户购买URL实体>
 * <功能详细描述>
 * 
 * @author  xp_Huang
 * @version  [版本号, 2017年7月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Entity
@Table(name = "BAO_T_SPECIAL_USERS_URL")
public class SpecialUsersUrlEntity extends BaseEntity {

	// <变量的意义、目的、功能和可能被用到的地方>
	private static final long serialVersionUID = -5094832174933519247L;

	@Column(name="PURCHASE_URL",length=300)
	@Setter
	@Getter
	/**
	 * 购买链接
	 */
	private String purchaseUrl;
	
	@Column(name="START_TIME")
	@Setter
	@Getter
	/**
	 * 生效时间
	 */
	private Date startTime;
	
	@Column(name="EXPIRE_DATE")
	@Setter
	@Getter
	/**
	 * 失效时间
	 */
	private Date expireDate;
	
	@Column(name="TOKEN",length=50)
	@Setter
	@Getter
	/**
	 * Token链接时效控制
	 */
	private String token;
}
