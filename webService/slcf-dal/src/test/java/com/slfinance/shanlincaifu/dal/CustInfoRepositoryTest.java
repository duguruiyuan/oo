package com.slfinance.shanlincaifu.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;


@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class CustInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private CustInfoRepository custInfoRepository;
	@Autowired 
	InvestInfoRepository  investInfoRepository;
	
	

	@Test
	public void getOwnerAmountGTest(){
		Map<String, Object> params=new HashMap<>();
		params.put("productName", "活期宝");
		params.put("custId", "20150427000000000000000000000000001");
		System.out.println(custInfoRepository.getOwnerAmount(params));
	}
	@Test
	public void findOwnerList(){
		Map<String, Object> params=new HashMap<>();
		Page<Map<String,Object>> list=custInfoRepository.findOwnerList(params);
		System.out.println(list);
	}
	
	@Test
	public void testFindByPrimaryKey() {
		Map<String, Object> map = new HashMap<>();
		map.put("mobile", "13567813457");
		map.put("loginPassword", "pwd1");
		CustInfoEntity file = custInfoRepository.findOne("409082584cc52a79014cc52a7ebe0000");
		assertNotNull(file);
		assertEquals("409082584cc52a79014cc52a7ebe0000", file.getId());
	}
	
	public void testFindByName() {
		Map<String, Object> map = new HashMap<>();
		map.put("mobile", "13567813457");
		map.put("loginPassword", "pwd1");
		CustInfoEntity file = custInfoRepository.findOne("20150414001");
		assertNotNull(file);
		assertEquals("13567813457", file.getMobile());
	}
	
	@Test
	public void testAccountAmountBycustIdAndProductType() {
		BigDecimal mm = investInfoRepository.accountAmountBycustIdAndProductType("01","活期宝");
		assertNotNull(mm);
	}
	
	@Test
	public void testFindByIdNotIn(){
		custInfoRepository.findByIdNotIn(Arrays.asList(Constant.CUST_ADMIN_ID,Constant.CUST_KF_ID,Constant.SYSTEM_USER_BACK));
	}
	
}
