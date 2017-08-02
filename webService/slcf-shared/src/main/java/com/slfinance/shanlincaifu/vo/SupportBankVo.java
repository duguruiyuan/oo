/** 
 * @(#)SupportBankVo.java 1.0.0 2014年11月26日 上午11:20:18  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.vo;

/**
 * 充值提现支持的银行
 * 
 * @author kongxiong
 * @version $Revision:1.0.0, $Date: 2014年11月26日 上午11:20:18 $
 */
public class SupportBankVo {

	public static final String CATEGORY_RECHARGE = "recharge";
	public static final String CATEGORY_WITHDRAW = "withdraw";
	public static final String CATEGORY_ALL = "all";

	private String code;
	private String name;
	private String logoPath;
	private int sort;
	private String category;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the logoPath
	 */
	public String getLogoPath() {
		return logoPath;
	}

	/**
	 * @param logoPath
	 *            the logoPath to set
	 */
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	/**
	 * @return the sort
	 */
	public int getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(int sort) {
		this.sort = sort;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		if(category == null){
			this.category = CATEGORY_ALL;
		}else{
			this.category = category;			
		}
	}

}
