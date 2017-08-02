package com.slfinance.shanlincaifu.dal;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 测试赎回数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月30日 下午1:38:39 $ 
 */
@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class CustActivityInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
	
	/**
	 * 测试通过客户ID查询
	 *
	 * @author  wangjf
	 * @date    2015年5月21日 下午4:56:54
	 */
	@Test
	public void testFindByCustId()
	{
		List<CustActivityInfoEntity> list = custActivityInfoRepository.findByCustId("5da996b9-713d-4fa0-94c6-ce1c2bed1476", Constant.REAWARD_SPREAD_01);
		assertNotNull(list);
	}
}
