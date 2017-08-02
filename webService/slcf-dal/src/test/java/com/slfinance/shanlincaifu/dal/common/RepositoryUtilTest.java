/** 
 * @(#)DataJpaServiceDemoTest.java 1.0.0 2015年4月14日 下午8:39:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.config.Task;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoginLogInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.SmsInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.searchFilter.DynamicSpecifications;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter.Operator;
import com.slfinance.shanlincaifu.utils.Constant;

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class RepositoryUtilTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private LoginLogInfoRepository loginLogInfoRepository;
	
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;

	@Autowired
	private TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;

	@Autowired
	private SmsInfoRepository smsInfoRepository;
	
	@PersistenceContext
	private EntityManager em;


	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Test
	public void aTest(){
		String sql = "update BAO_T_SMS_INFO set target_address=target_address where target_address=:addr";
//		final String[] loanIds = new String[] { "13564195002", "13111171111", "1" };
//		BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
//			public int getBatchSize() {
//				return loanIds.length;
//			}
//			public void setValues(PreparedStatement ps, int i) {
//				try {
//					ps.setString(1, loanIds[i]);
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//		int[] _results = jdbcTemplate.batchUpdate(sql, setter);
//		for (int result : _results) {
//			System.out.println(result);
//		}
		
//		final List<Object[]> batchArgs = new ArrayList<Object[]>();
//		batchArgs.add(new Object[]{"13564195002"});
//		batchArgs.add(new Object[]{"13111171111"});
//		batchArgs.add(new Object[]{"1"});
//		int[] _results =jdbcTemplate.batchUpdate(sql, batchArgs);
//		for (int result : _results) {
//			System.out.println(result);
//		}
		
		
		int[] results = jdbcTemplate.batchUpdate("update BAO_T_SMS_INFO set target_address=target_address where target_address='13564195002'", "update BAO_T_SMS_INFO set target_address=target_address where target_address='13111171111'", "update BAO_T_SMS_INFO set target_address=target_address where target_address='2'");
		for (int result : results) {
			System.out.println(result);
		}
	}
	
	
	
	@Test
	public void dynamicDAOTest(){
		Map<String, Object> searchParams=new HashMap<String, Object>();
		PageRequest pageRequest = buildPageRequest(1, 10, "");
	    
	    Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        filters.put("targetType", new SearchFilter("targetType", Operator.EQ, Constant.SMS_TYPE_REGISTER));
	    Page page=smsInfoRepository.findAll(buildSpecification(filters),pageRequest);
	    System.out.println(page.getSize());
	}
	
	 /**
     * 创建分页请求.
     */
    private PageRequest buildPageRequest(int pageNumber, int pagzSize, String sortType) {
        Sort sort = null;
        if ("auto".equals(sortType)) {
            sort = new Sort(Direction.DESC, "id");
        } else if ("title".equals(sortType)) {
            sort = new Sort(Direction.ASC, "title");
        }
        return new PageRequest(pageNumber - 1, pagzSize, sort);
    }
 
    /**
     * 创建动态查询条件组合.
     */
    private Specification<Task> buildSpecification(String targetType, Map<String, Object> searchParams) {
        Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        filters.put("targetType", new SearchFilter("targetType", Operator.EQ, targetType));
        Specification<Task> spec = DynamicSpecifications.bySearchFilter(filters.values(), Task.class);
        return spec;
    }
    
    private Specification<Task> buildSpecification(Map<String, SearchFilter> filters) {
        Specification<Task> spec = DynamicSpecifications.bySearchFilter(filters.values(), Task.class);
        return spec;
    }
    
	@Test
	public void queryForMapByJPQLTest(){
		//jpql查询,返回map格式
		Query query=em.createQuery("select new map( c.targetType as a,c.sendContent as b) from SmsInfoEntity c");
		List<Map<String,Object>> mapList=query.getResultList();
		for(Map<String,Object> map:mapList){
			System.out.println(map.get("a")+","+map.get("b"));
		}
	}
	
	@Test
	public void queryForPageMapTest() throws RuntimeException{
		String sql="select id,bank_name from BAO_T_BANK_CARD_INFO order by id asc";
		Object[] args=new Object[]{};
		int pageNum=1;
		int pageSize=10;
		List<Map<String, Object>> listMap=repositoryUtil.queryForPage(sql, args, pageNum, pageSize);
		for(Map<String, Object> map:listMap){
			for(String key:map.keySet()){
				System.out.println("key:"+key+",value:"+map.get(key));
			}
		}
	}


	@Test
	public void queryForListTest() throws RuntimeException{
		String sql="select id,bank_name from BAO_T_BANK_CARD_INFO order by id asc";
		List<BankCardInfoEntity> list=
				repositoryUtil.queryForList(sql, new Object[]{}, BankCardInfoEntity.class);
		for(BankCardInfoEntity bcie:list){
			System.out.println(bcie.getId()+","+bcie.getBankName());
		}
		
	}

	@Test
	public void queryForPageTest(){
		String sql="select id,bank_name from BAO_T_BANK_CARD_INFO order by id asc";
		Object[] args=new Object[]{};
		int pageNum=1;
		int pageSize=5;
		
		Page<BankCardInfoEntity> list=
				repositoryUtil
				.queryForPage(sql, args, pageNum, pageSize, BankCardInfoEntity.class);
		System.out.println(list.getContent().size());
		for(BankCardInfoEntity vo:list.getContent()){
			System.out.println(vo.getId()+"--->"+vo.getBankName());
		}
		
	}
	
	/**根据用户id和产品类型查询持有金额*/
	@Test
	public void testAccountAmountBycustIdAndProductType() {
		investInfoRepository.accountAmountBycustIdAndProductType("01","活期宝");
	}
	/**根据用户id和产品类型查询持有金额*/
	@Test
	public void estInvestAmountBycustIdAndProductType() {
		List<Object[]> list=investInfoRepository.investAmountBycustIdAndProductType("01","活期宝");
		System.out.println(list.size());
	}
	
	/**上次登录时间*/
	@Test
	public void testFindLastLoginInfoOrderByLoginDateDesc() {
		List<Object[]> list=loginLogInfoRepository.findLastLoginInfoOrderByLoginDateDesc("01", new Date());
		System.out.println(list.size());
	}
	
	/**根据用户id和交易类型汇总所有交易金额*/
	@Test
	public void testFindTrade() {
		List<TradeFlowInfoEntity> list=tradeFlowInfoRepositoryCustom.findTrade("01", new Date(),new Date(),"活期宝");
		System.out.println(list.size());
	}

	@Autowired
	private CustInfoRepository custInfoRepository;
    @Test
	public void test001(){
//    	Page<Map<String, Object>> param =custInfoRepository.findCustInfoEntityByPage(null);
	}
	
}
