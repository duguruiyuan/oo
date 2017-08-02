package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;



/**
 * ExportFileService Test. @author Tools
 */
public class ExportFileServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private ExportFileService exportFileService;


	/**
	 * 测试下载企业借款协议
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-07 19:35:03
	 */
	@Test
	public void testDownloadProjectContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("projectId", "20160227124811");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = exportFileService.downloadProjectContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试下载优选计划协议
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-07 19:35:03
	 */
	@Test
	public void testDownloadWealthContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "6685b1c1-fadf-48e6-881a-997231d47e7b");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = exportFileService.downloadWealthContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试下载优选计划债权协议
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-07 19:35:03
	 */
	@Test
	public void testDownloadWealthLoanContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("wealthId", "6685b1c1-fadf-48e6-881a-997231d47e7b");
		param.put("loanId", "cddddc3c-955d-44f5-bd13-86b7513e68eb");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = exportFileService.downloadWealthLoanContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}


	/**
	 * 测试下载优选项目协议
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午7:14:12
	 * @throws SLException
	 */
	@Test
	public void testDownloadLoanContract() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanId", "9f4fa60f-79bd-46b7-90c8-442f2ea29b94");
		param.put("custId", "050264d5-baaf-4779-874f-66780c91d81b");
		ResultVo result = exportFileService.downloadLoanContract(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
}