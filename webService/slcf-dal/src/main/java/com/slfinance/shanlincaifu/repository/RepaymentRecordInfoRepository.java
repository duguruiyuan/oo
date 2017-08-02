/** 
 * @(#)RepaymentRecordInfoRepository.java 1.0.0 2015年5月1日 下午4:24:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.RepaymentRecordInfoEntity;

/**   
 * 还款记录数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午4:24:06 $ 
 */
public interface RepaymentRecordInfoRepository extends PagingAndSortingRepository<RepaymentRecordInfoEntity, String>{

	/**
	 * 取已分配未处理的还款记录
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午4:34:37
	 * @return
	 */
	@Query(" select A from RepaymentRecordInfoEntity A where A.handleStatus = ? "
			+ " and A.loanId IN ("
			+ " select B.loanId from AllotDetailInfoEntity B "
			+ " where B.allotId in ("
			+ "       select C.id from AllotInfoEntity C"
			+ "       where C.allotStatus != '已撤销' and relatePrimary in ("
			+ "           select D.id from ProductTypeInfoEntity D"
			+ "           where D.typeName = ? "
			+ "      ) "
			+ "    )"
			+ " )"
			+ " order by A.createDate ASC")
	List<RepaymentRecordInfoEntity> queryUnHandleAllot(String handleStatus, String typeName);
	
	/**
	 * 取未分配未处理的还款记录
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午4:34:37
	 * @return
	 */
	@Query(" select A from RepaymentRecordInfoEntity A where A.handleStatus = ? "
			+ " and A.loanId NOT IN ("
			+ " select B.loanId from AllotDetailInfoEntity B "
			+ " where B.allotId in ("
			+ "       select C.id from AllotInfoEntity C"
			+ "       where C.allotStatus != '已撤销' "
			+ "    )"
			+ " )"
			+ " order by A.createDate ASC")
	List<RepaymentRecordInfoEntity> queryUnHandleNotAllot(String handleStatus);
	
	
}
