package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;


/**   
 * BAO项目投资情况表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-01-05 13:36:57 $ 
 */
public interface ProjectInvestInfoRepository extends PagingAndSortingRepository<ProjectInvestInfoEntity, String>{

	/**
	 * 通过项目Id查询投资情况
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午4:29:17
	 * @param projectId
	 * @return
	 */
	ProjectInvestInfoEntity findByProjectId(String projectId);
	
	/**
	 * 通过理财计划id查询投资情况
	 * 
	 * @author zhiwen_feng
	 * @param wealthId
	 * @return
	 */
	ProjectInvestInfoEntity findByWealthId(String wealthId);
	
	/**
	 * 通过借款id查询投资情况
	 * @author lyy
	 * @param loanId
	 * @return ProjectInvestInfoEntity
	 */
	ProjectInvestInfoEntity findByLoanId(String loanId);
}
