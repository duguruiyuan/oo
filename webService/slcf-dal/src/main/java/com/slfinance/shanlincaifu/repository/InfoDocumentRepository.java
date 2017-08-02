package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.InfoDocumentEntity;

/**
 * 
 * <信息披露数据访问接口>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月19日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface InfoDocumentRepository extends PagingAndSortingRepository<InfoDocumentEntity, String>{

}
