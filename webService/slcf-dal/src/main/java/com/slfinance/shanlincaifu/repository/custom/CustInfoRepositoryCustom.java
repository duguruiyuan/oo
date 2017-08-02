package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * 查询客户信息
 * 
 * @author zhumin
 *
 */
public interface CustInfoRepositoryCustom {
	
	/**
	 * 用户持有份额：用户在对活期宝持有的份额
	 * @param params
	 * @return
	 */
	public BigDecimal getOwnerAmount(Map<String, Object> params);
	
	/**
	 * 用户定期宝产品可购买额度 
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 * 	  	 <ul>
	 *	     	<li>custId	用户ID		{@link java.lang.String}</li>
	 *	     	<li>id		产品ID		{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return
	 * 		<li>{@link java.math.BigDecimal} 返回结果</li>
	 * 
	 * @throws SLException
	 */
	public BigDecimal getUserHoldAmount(String custId,String productId) throws SLException;
	
	public Map<String, Object> countOwnerList(Map<String, Object> params);
	public Page<Map<String,Object>> findOwnerList(Map<String, Object> params);

	/** 查询客户信息列表 */
	public Page<Map<String, Object>> findCustInfoEntityByPage(Map<String, Object> param);
	
	/** 统计客户邀请信息 */
	public ResultVo queryInviteData(Map<String,Object> custMap) throws SLException;
	/** 根据邀请级别查询客户*/
	public ResultVo queryCustSpreadLevel(Map<String,Object> custMap) throws SLException;
	
	/**
	 * 用户详细信息查看
	 * 
	 * 资金信息
	 * 用户信息
	 * 
	 */
	public Map<String,Object> findCustInfoEntityDetail(Map<String,Object> custMap) throws SLException;

	/**
	 * 更新客户以防止重复提交
	 *
	 * @author  wangjf
	 * @date    2015年11月11日 上午11:15:29
	 * @param custId
	 * @param flag true：设置标志位 false：取消标志位
	 * @return
	 */
	public int updateCustInfoToAvoidDuplicate(String custId, boolean flag);
	
	/**
	 * 统计注册次数
	 * @author zhangt
	 * @date    2015年12月10日 下午18:55:29
	 * @return
	 */
	public java.util.List<Map<String,Object>> findRigsterCount();

	/**
	 * 公司列表
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 上午11:50:43
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> findCompanyList(Map<String, Object> params);
	
	/**
	 * 判断用户是否是业务员
	 * 
	 * @param params
	 * @return
	 */
	public Boolean judgeUserIsEmployee(Map<String, Object> params);
	
	/**
	 * 离职
	 * 
	 * @param params
	 * @return
	 */
	public int leaveJob(Map<String, Object> params);

	/**
	 * 企业商户-商户列表
	 * @author liyy
	 * @date   2016年12月16日下午15:30:00
	 * @param param
	 *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
	 * @return
     *      <tt>custId                :String:客户编号</tt><br>
     *      <tt>custName              :String:姓名</tt><br>
     *      <tt>accountAvailableAmount:String:账户余额</tt><br>
     *      <tt>bankName              :String:银行卡名称</tt><br>
     *      <tt>cardNo                :String:银行卡卡号</tt><br>
     *      <tt>tel                   :String:联系电话（客户表联系电话）</tt><br>
     *      <tt>address               :String:地址（客户表通讯地址）</tt><br>
	 */
	public Page<Map<String, Object>> queryCompanyUserList(
			Map<String, Object> params);

    /**
     * 查询指定用户第一次达到每个等级的日期和等级
     * @author mali
     * @date   2017-6-9 15:19:44
     *
     * @param custId 用户id
     *
     * @return
     */
    List<Map<String,Object>> getFirstAchievingDateAndRanking(String custId);

    /**
     * 查询首次达到当前等级的时间段的最后时间
     *
     * @author mali
     * @date   2017-6-9 15:19:44
     * @param custId        用户id
     * @param ranking       等级
     * @param nextBeginDate 该等级发生变动后的时间段的首日   @return
     */
    List<Map<String,Object>> getEndDateOfFirstAchieving(String custId, int ranking, String nextBeginDate);
    
    /**
     * @author zhangyb
     * @param paraMap 
     * @param mobile        用户手机号
     * 
     * @return
     */
    public int updateOpenIdByMobile(Map<String, Object> paraMap);
}