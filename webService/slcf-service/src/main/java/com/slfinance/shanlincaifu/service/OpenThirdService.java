package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * 针对第三方的查询、操作等服务
 * 
 * @author Administrator
 *
 */
public interface OpenThirdService {

	/**
	 * 绑定银行卡
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午4:26:54
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo bindBankNotify(Map<String, Object> params) throws SLException;
	
	/**
	 * 解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午4:26:54
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo unBindBankNotify(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询实名认证
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午4:27:58
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo isRealName(Map<String, Object> params) throws SLException;
	
	/**
	 * 登录(仅供测试使用)
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午4:28:43
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo login(Map<String, Object> params) throws SLException;
	
	/**
	 * 放款通知(内部使用)
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 下午3:21:33
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo nofity(Map<String, Object> params) throws SLException;
	
	/**
	 * 发送通知(给商务发送通知)
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午5:06:25
	 * @param url
	 * @param requestMap
	 * @return
	 */
	public Map<String, Object> sendNotify(String url, Map<String, Object> requestMap);
	


		
	
}
