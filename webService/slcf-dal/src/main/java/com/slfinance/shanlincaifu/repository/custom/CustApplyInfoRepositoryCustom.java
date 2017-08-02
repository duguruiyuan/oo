package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AgentEntity;


/**
 * 善林财富-客户管理
 * 
 * @author liyy
 * @version $Revision:1.0.0, $Date: 2016年2月26日 下午3:06:28 $
 */
public interface CustApplyInfoRepositoryCustom {
	
	/**
	 * 线下业务-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>userId         :String:客户经理ID</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return Page 
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *  @throws SLException
	 */
	Page<Map<String, Object>> queryAllCustTransferList(Map<String, Object> param);
	

	/**
	 * 根据客户经理id取agent信息
	 * @param userId String 客户经理id
	 * @return AgentEntity
	 */
	List<AgentEntity> getAgentEntityByUserId(String userId);

	/**
	 * 线下业务-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>userId         :String:客户经理ID</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     * @param dataPermission String 数据权限
	 * @return Page 
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *  @throws SLException
	 */
	Page<Map<String, Object>> queryAllCustTransferListNew(
			Map<String, Object> param, String dataPermission);

	/**
	 * 前台-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>userId          :String:客户经理ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return Page 
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *  @throws SLException
	 */
	Page<Map<String, Object>> queryCustTransferList(Map<String, Object> param);

	/**
	 * 线下业务/前台-客户转移-客户转移查看详情-基本信息
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId：客户申请ID</tt><br>
	 * @return List<Map<String, Object>> 
     *      	<tt>custApplyId                  :String:客户申请ID</tt><br>
     *      	<tt>custId                       :String:用户ID</tt><br>
     *      	<tt>loginName                    :String:用户名</tt><br>
     *      	<tt>custName                     :String:客户姓名</tt><br>
     *      	<tt>credentialsCode              :String:证件号码</tt><br>
     *      	<tt>mobile                       :String:手机号</tt><br>
     *      	<tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      	<tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      	<tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      	<tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      	<tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      	<tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      	<tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      	<tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     */
	List<Map<String, Object>> queryCustTransferDetailById(
			Map<String, Object> param);

	/**
	 * 线下业务-客户转移-客户转移查看详情-附件信息
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param tableName String RELATE_TYPE
	 * @param relId String RELATE_PRIMARY
	 * @return List :attachmentList
     *      	<tt>attachmentType:String:附件类型</tt><br>
     *      	<tt>attachmentName:String:附件名称</tt><br>
     *      	<tt>storagePath   :String:存储路径</tt><br>
     */
	List<Map<String, Object>> queryAttachmentInfoListById(
			String tableName, String relId);

	/**
	 * 审核信息
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param logType String 日志类型
	 * @param relType String 关联类型
	 * @param relId String 关联ID
	 * @return List :auditList
     *      	<tt>auditId    :String:审核ID</tt><br>
     *      	<tt>auditDate  :String:审核时间</tt><br>
     *      	<tt>auditUser  :String:审核人员</tt><br>
     *      	<tt>auditStatus:String:审核结果</tt><br>
     *      	<tt>auditMemo  :String:审核备注</tt><br>
     */
	List<Map<String, Object>> queryAuditInfoListById(String logType, String relType, String relId);

	/**
	 * 我是业务员-客户管理-客户列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
     *      <tt>custId           :String:客户ID（客户经理）</tt><br>
     *      <tt>custName         :String:用户名（可以为空）</tt><br>
     *      <tt>credentialsCode  :String:证件号码（可以为空）</tt><br>
     *      <tt>mobile           :String:手机号（可以为空）</tt><br>
     *      <tt>beginRegisterDate:String:开始注册时间（可以为空）</tt><br>
     *      <tt>endRegisterDate  :String:结束注册时间（可以为空）</tt><br>
     * @param isSelf boolean:原客户经理发起
	 * @return Page:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	Page<Map<String, Object>> queryCustByManager(Map<String, Object> param);

	/**
	 * 我是业务员-客户管理-投资信息查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return Page:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>investDate  :String:投资时间</tt><br>
     *      		<tt>investAmount:String:投资金额</tt><br>
     *      		<tt>lendingType :String:计划名称</tt><br>
     *      		<tt>lendingNo   :String:项目期数</tt><br>
     *      		<tt>typeTerm    :String:项目期限(月)</tt><br>
     *      		<tt>yearRate    :String:年化收益率</tt><br>
     *      		<tt>awardRate   :String:奖励利率</tt><br>
     *      		<tt>effectDate  :String:生效日期</tt><br>
     *      		<tt>endDate     :String:到期日期</tt><br>
     *      		<tt>investStatus:String:投资状态（当前状态）</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	Page<Map<String, Object>> queryCustWealthByManager(Map<String, Object> param);

	/**
	 * 我是业务员-附属银行卡-客户银行卡列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return Page:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>bankName       :String:银行名称</tt><br>
     *      		<tt>bankCardNo     :String:银行卡号</tt><br>
     *      		<tt>branchBankName :String:支行名称</tt><br>
     *      		<tt>openProvince   :String:开户行所在省</tt><br>
     *      		<tt>openCity       :String:开户行所在市</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	Page<Map<String, Object>> queryCustBankByManager(Map<String, Object> param);

	
	/**
	 * 我是业务员-附属银行卡-查看客户银行卡-基本信息
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
	 * @return List< Map< String, Object>> <br>
     *      	<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      	<tt>custId         :String:客户ID</tt><br>
     *      	<tt>custName       :String:用户名</tt><br>
     *      	<tt>credentialsCode:String:证件号码</tt><br>
     *      	<tt>mobile         :String:手机号</tt><br>
     *      	<tt>bankCode       :String:银行名称code</tt><br>
     *      	<tt>bankName       :String:银行名称</tt><br>
     *      	<tt>bankCardNo     :String:银行卡号</tt><br>
     *      	<tt>branchBankName :String:支行名称</tt><br>
     *      	<tt>openProvince   :String:开户行所在省code</tt><br>
     *      	<tt>openProvinceName   :String:开户行所在省</tt><br>
     *      	<tt>openCity       :String:开户行所在市code</tt><br>
     *      	<tt>openCityName       :String:开户行所在市</tt><br>
     *      	<tt>tradeStatus       :String:当前状态</tt><br>
     */
	List<Map<String, Object>> queryCustBankDetailByManager(
			Map<String, Object> param);

	/**
	 * 根据类型和code,取名称
	 * @author  liyy
	 * @param type :String:类型
	 * @param code :String:code
	 * @return String
	 */
	String getNameByCode(String type, String code);

	/**
	 * 判断同一客户同一卡号不能重复存在
	 * @author  liyy
	 * @param custId :String:客户Id
	 * @param cardNo :String:卡号
	 * @return int
	 */
	int checkCustAndCardNo(String custId, String cardNo);

	/**
	 * 校验转移是否存在
	 * @author  liyy
	 * @param mgrId :String:原客户经理
	 * @param custId :String:客户Id
	 * @return int
	 */
	int countCheckMgrAndCust(String mgrId, String custId);

}
