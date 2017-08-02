package com.slfinance.shanlincaifu.repository;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;


/**   
 * BAO项目信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-01-05 13:36:57 $ 
 */
public interface ProjectInfoRepository extends PagingAndSortingRepository<ProjectInfoEntity, String>{
	
	/**
	 * 根据项目编号查询
	 * 
	 * @param projectNo
	 * @return
	 */
	public ProjectInfoEntity findByProjectNo(String projectNo);
	
	/**
	 * 根据公司名称和资产类型查询
	 * 
	 * @param companyName
	 * @param projectType
	 * @return
	 */
	public ProjectInfoEntity findByCompanyNameAndProjectType(String companyName, String projectType);

	/**
	 * 根据客户ID查询投资信息(投资次数)
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:47:43
	 * @param custId
	 * @return
	 */
	@Query("select count(A.id) from InvestInfoEntity A where A.custId = :custId and A.projectId = :projectId")
	public int countInvestInfoByCustId(@Param("custId")String custId, @Param("projectId")String projectId);
	
	/**
	 * 通过客户ID和投资日期查询投资
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:47:02
	 * @param custId
	 * @param investDate
	 * @return
	 */
	@Query("select A from InvestInfoEntity A where A.investStatus = :investStatus and A.custId = :custId and A.projectId = :projectId ")
	public InvestInfoEntity findInvestInfoByCustIdAndInvestDate(@Param("custId")String custId,  @Param("projectId")String projectId,  @Param("investStatus")String investStatus);
	
	/**
	 * 根据项目期限和项目状态统计
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午10:27:35
	 * @param typeTerm
	 * @param projectStatus
	 * @return
	 */
	public int countByTypeTermAndProjectStatus(Integer typeTerm, String projectStatus);
	
	/**
	 * 查询待发布项目
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午10:36:28
	 * @param projectStatus
	 * @param releaseDate
	 * @return
	 */
	@Query(value = "select * from BAO_T_PROJECT_INFO A where A.PROJECT_STATUS = :projectStatus and trunc(A.RELEASE_DATE) = trunc(:releaseDate) order by A.CREATE_DATE", nativeQuery = true)
	public List<ProjectInfoEntity> findWaitingPublishProject(@Param("projectStatus")String projectStatus, @Param("releaseDate")Date releaseDate);
	
	/**
	 * 通过项目查询投资信息
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午11:18:52
	 * @param projectId
	 * @return
	 */
	@Query("select A from InvestInfoEntity A where projectId = :projectId")
	public List<InvestInfoEntity> findInvestByProjectId(@Param("projectId")String projectId);
	
	/**
	 * 通过项目查询账户信息
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午11:34:37
	 * @param projectId
	 * @return
	 */
	@Query("select A from AccountInfoEntity A where A.custId in (select B.custId from InvestInfoEntity B where projectId = :projectId)")
	public List<AccountInfoEntity> findAccountByProjectId(@Param("projectId")String projectId);
	
	/**
	 * 查询待流标项目
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午10:36:28
	 * @param projectStatus
	 * @param releaseDate
	 * @return
	 */
	@Query(value = "select * from BAO_T_PROJECT_INFO A where (A.PROJECT_STATUS = :projectStatus OR A.PROJECT_STATUS = :newProjectStatus) and A.EFFECT_DATE < :unReleaseDate ", nativeQuery = true)
	public List<ProjectInfoEntity> findWaitingUnReleaseProject(@Param("projectStatus")String projectStatus, @Param("unReleaseDate")Date unReleaseDate, @Param("newProjectStatus")String newProjectStatus);
	
	/**
	 * 查询待生效项目
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午10:36:28
	 * @param projectStatus
	 * @param releaseDate
	 * @return
	 */
	@Query(value = "select * from BAO_T_PROJECT_INFO A where A.PROJECT_STATUS = :projectStatus and trunc(A.EFFECT_DATE) = trunc(:unReleaseDate)", nativeQuery = true)
	public List<ProjectInfoEntity> findWaitingReleaseProject(@Param("projectStatus")String projectStatus, @Param("unReleaseDate")Date unReleaseDate);
	
	/**
	 * 通过项目查询还款信息
	 *
	 * @author  wangjf
	 * @date    2016年1月7日 下午3:42:36
	 * @param projectId
	 * @return
	 */
	@Query(value = "select A from RepaymentPlanInfoEntity A where A.projectId = :projectId order by A.currentTerm")
	public List<RepaymentPlanInfoEntity> findRepaymentByProjectId(@Param("projectId")String projectId);
	
	/**
	 * 通过项目和还款状态查询还款信息
	 *
	 * @author  wangjf
	 * @date    2016年1月7日 下午3:42:36
	 * @param projectId
	 * @return
	 */
	@Query(value = "select A from RepaymentPlanInfoEntity A where A.projectId = :projectId and A.repaymentStatus = :repaymentStatus order by A.currentTerm")
	public List<RepaymentPlanInfoEntity> findRepaymentByProjectIdAndRepaymentStatus(@Param("projectId")String projectId, @Param("repaymentStatus")String repaymentStatus);
	
	/**
	 * 根据项目名查询
	 * @author zhangt
	 * @param projectName
	 * @return
	 */
	public ProjectInfoEntity findByProjectName(String projectName);
	
	/**
	 * 通过项目状态和发布日期查询
	 * 
	 * @author zhangt
	 * @param projectStatus
	 * @param releaseDate
	 * @return
	 */
	@Query(value = "select * from BAO_T_PROJECT_INFO A where A.PROJECT_STATUS = :projectStatus and trunc(A.RELEASE_DATE) <= trunc(:releaseDate) order by A.CREATE_DATE", nativeQuery = true)
	public List<ProjectInfoEntity> findWaitAuditRefuseProject(@Param("projectStatus")String projectStatus, @Param("releaseDate")Date releaseDate);
}
