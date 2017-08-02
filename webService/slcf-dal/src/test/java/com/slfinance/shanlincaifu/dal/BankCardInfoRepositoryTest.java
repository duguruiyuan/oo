package com.slfinance.shanlincaifu.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class BankCardInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private BankCardInfoRepository bankCardInfoRepository;


	@Test
	public void testFindByPrimaryKey() {
		BankCardInfoEntity file = bankCardInfoRepository.findOne("409082584cc52a79014cc52a7ecc0001");
		assertNotNull(file);
		assertEquals("409082584cc52a79014cc52a7ecc0001", file.getId());
	}
	
	@Test
	public void testSaveValidator() {
		BankCardInfoEntity file = bankCardInfoRepository.findOne("409082584cc52a79014cc52a7ecc0001");
		file.setCardNo("436");
		file = bankCardInfoRepository.save(file);
		assertNotNull(file);
		assertEquals("409082584cc52a79014cc52a7ecc0001", file.getId());
		assertEquals("123", file.getCardNo());
	}
	
	@Test
	public void testFindByCustIdOrderByCreateDateDesc(){
		List<BankCardInfoEntity> list = bankCardInfoRepository.findByCustIdOrderByCreateDateDesc("409082584cc52a79014cc52a7ebe0000");
		assertNotNull(list);
		assertEquals(list.size() == 0, false);
	}
}
