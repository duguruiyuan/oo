/** 
 * @(#)AfficheInfoServiceImpl.java 1.0.0 2015年4月28日 下午5:14:34  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AfficheInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageReadInfoEntity;
import com.slfinance.shanlincaifu.repository.AfficheInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageReadInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.service.AfficheInfoService;
import com.slfinance.shanlincaifu.service.SystemMessageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.ImgSrcReplaceUtil;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.util.BeanMapConvertUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 网站公告业务实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午5:14:34 $ 
 */
@Service(value="afficheInfoService")
@Transactional(readOnly=true)
public class AfficheInfoServiceImpl implements AfficheInfoService {

	@Autowired
	private SystemMessageService systemMessageService;
	
	@Autowired
	private AfficheInfoRepository afficheInfoRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private SystemMessageReadInfoRepository systemMessageReadInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	/**
	 * 分页查询网站公告列表(修改为jdbc方式查询)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Cacheable(value="slcf_afficheSearch",key="'afficheSearch_' + #paramsMap['cache']",  condition="#paramsMap['cache']!=null", cacheManager="redis")
	public Map<String,Object> findAllAffiche(Map<String, Object> paramsMap) throws SLException {
		
		if(StringUtils.isEmpty((String)paramsMap.get("custId"))) {
			AfficheInfoEntity affi = BeanMapConvertUtil.toBean(AfficheInfoEntity.class, paramsMap);
			String baseColumn = " A.ID,A.AFFICHE_TITLE,A.AFFICHE_TYPE,A.AFFICHE_CONTENT,A.AFFICHE_STATUS,A.SORT_NUM,A.PUBLISH_TIME,A.RECORD_STATUS,A.CREATE_DATE ,A.LAST_UPDATE_USER,A.LAST_UPDATE_DATE,A.VERSION,A.MEMO ";
			String createUserColumn = " DECODE(U.USER_NAME,NULL,A.CREATE_USER,U.USER_NAME) AS CREATE_USER ";
			String publishUserColumn = " DECODE(A.PUBLISH_USER,NULL,A.PUBLISH_USER, DECODE((SELECT USER_NAME FROM COM_T_USER WHERE ID = A.PUBLISH_USER ),NULL,A.PUBLISH_USER,(SELECT USER_NAME FROM COM_T_USER WHERE ID = A.PUBLISH_USER )) ) AS PUBLISH_USER ";
			StringBuffer column = new StringBuffer().append(baseColumn).append(",").append(createUserColumn).append(",").append(publishUserColumn);
			StringBuffer sql = new StringBuffer(" SELECT ").append(column).append(" FROM BAO_T_AFFICHE_INFO A LEFT JOIN COM_T_USER U ON A.CREATE_USER = U.ID ");
			StringBuilder condition = new StringBuilder();
			List<Object> objList=new ArrayList<>(); 
			
			//公告类型
			if (StringUtils.isNotEmpty(affi.getAfficheType())){
				PageFuns.buildWhereSql(condition).append(" A.AFFICHE_TYPE=? ");
				objList.add(affi.getAfficheType());
			}
			//默认不显示已删除状态的数据信息
			if (StringUtils.isEmpty(affi.getAfficheStatus()) || ( StringUtils.isNotEmpty(affi.getAfficheStatus()) && Constant.AFFICHE_STATUS_DELETED.equals(affi.getAfficheStatus())) ){
				PageFuns.buildWhereSql(condition).append(" A.AFFICHE_STATUS != ? ");
				objList.add(Constant.AFFICHE_STATUS_DELETED);
			}
			
			//状态
			if (StringUtils.isNotEmpty(affi.getAfficheStatus()) && !Constant.AFFICHE_STATUS_DELETED.equals(affi.getAfficheStatus())){
				PageFuns.buildWhereSql(condition).append(" A.AFFICHE_STATUS = ? ");
				objList.add(affi.getAfficheStatus());
			}
			
			//显示行业新闻和媒体动态
			if (null != paramsMap.get("statusList") && ((List<String>)paramsMap.get("statusList")).size() > 0){
				PageFuns.buildWhereSql(condition).append(" A.AFFICHE_TYPE IN  ").append(PageFuns.buildWhereInParams((List<String>)paramsMap.get("statusList"),objList));
			}
			
			//标题
			if (StringUtils.isNotEmpty(affi.getAfficheTitle())){
				PageFuns.buildWhereSql(condition).append(" A.AFFICHE_TITLE =? ");
				objList.add(affi.getAfficheTitle());
			}
			Page<AfficheInfoEntity> page = repositoryUtil.queryForPage(sql.append(condition).append(" ORDER BY A.PUBLISH_TIME DESC,A.CREATE_DATE DESC").toString(), objList.toArray(), (int)paramsMap.get("start"), (int)paramsMap.get("length"),AfficheInfoEntity.class);
			return PageFuns.pageVoToMap(page);
		}
		else { // 若包含客户ID
			List<Object> objList=new ArrayList<>(); 
			StringBuilder sqlString = new StringBuilder();
			sqlString.append(" SELECT A.ID \"id\",A.AFFICHE_TITLE \"afficheTitle\",A.AFFICHE_TYPE \"afficheType\", ")
			.append("        A.AFFICHE_CONTENT \"afficheContent\",A.AFFICHE_STATUS \"afficheStatus\",A.SORT_NUM \"sortNum\", ")
			.append("        A.PUBLISH_TIME \"publishTime\",A.RECORD_STATUS \"recordStatus\",A.CREATE_DATE \"createDate\", ")
			.append("        A.LAST_UPDATE_USER \"lastUpdateUser\",A.LAST_UPDATE_DATE \"lastUpdateDate\",A.VERSION \"version\", ")
			.append("        A.MEMO \"memo\", NVL(MES.IS_READ, '未读') \"isRead\", ")
			.append("        DECODE(U.USER_NAME,NULL,A.CREATE_USER,U.USER_NAME) \"createUser\", ")
			.append("        DECODE(A.PUBLISH_USER,NULL,A.PUBLISH_USER, DECODE((SELECT USER_NAME FROM COM_T_USER WHERE ID = A.PUBLISH_USER ),NULL,A.PUBLISH_USER,(SELECT USER_NAME FROM COM_T_USER WHERE ID = A.PUBLISH_USER )) ) \"publishUser\" ")
			.append(" FROM BAO_T_AFFICHE_INFO A LEFT JOIN COM_T_USER U ON A.CREATE_USER = U.ID ")
			.append(" LEFT JOIN BAO_T_SYSTEM_MESSAGE_READ_INFO MES ON MES.RELATE_PRIMARY = A.ID AND MES.RECEIVE_CUST_ID = ?  ")
			.append(" WHERE 1=1 ");
			objList.add((String)paramsMap.get("custId"));	
			
			//公告类型
			if (StringUtils.isNotEmpty((String)paramsMap.get("afficheType"))){
				sqlString.append("AND A.AFFICHE_TYPE=? ");
				objList.add((String)paramsMap.get("afficheType"));
			}
			//默认不显示已删除状态的数据信息
			if (StringUtils.isEmpty((String)paramsMap.get("afficheStatus")) 
					|| ( StringUtils.isNotEmpty((String)paramsMap.get("afficheStatus")) && Constant.AFFICHE_STATUS_DELETED.equals((String)paramsMap.get("afficheStatus")))){
				sqlString.append("AND A.AFFICHE_STATUS != ? ");
				objList.add(Constant.AFFICHE_STATUS_DELETED);
			}
			
			//状态
			if (StringUtils.isNotEmpty((String)paramsMap.get("afficheStatus")) 
					&& !Constant.AFFICHE_STATUS_DELETED.equals((String)paramsMap.get("afficheStatus"))){
				sqlString.append("AND A.AFFICHE_STATUS = ? ");
				objList.add((String)paramsMap.get("afficheStatus"));
			}
			
			//显示行业新闻和媒体动态
			if (null != paramsMap.get("statusList") && ((List<String>)paramsMap.get("statusList")).size() > 0){
				sqlString.append("AND A.AFFICHE_TYPE IN  ").append(PageFuns.buildWhereInParams((List<String>)paramsMap.get("statusList"),objList));
			}
			
			//标题
			if (StringUtils.isNotEmpty((String)paramsMap.get("afficheTitle"))){
				sqlString.append("AND A.AFFICHE_TITLE =? ");
				objList.add((String)paramsMap.get("afficheTitle"));
			}
			
			sqlString.append(" ORDER BY A.PUBLISH_TIME DESC, A.CREATE_DATE DESC");
			
			Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.valueOf(paramsMap.get("start").toString()), Integer.valueOf(paramsMap.get("length").toString()));
			return PageFuns.pageVoToMap(page);
		}
	}

	/**
	 * 网站公告--新增
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_afficheSearch", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_afficheCount", allEntries = true, cacheManager="redis") })
	public ResultVo createAffiche(AfficheInfoEntity afficheInfo) throws SLException{
		afficheInfo.setAfficheStatus(Constant.AFFICHE_STATUS_NEW);
		afficheInfo.setRecordStatus(Constant.VALID_STATUS_VALID);
		afficheInfo.setBasicModelProperty(afficheInfo.getCreateUser(), true);
		String id = afficheInfoRepository.save(afficheInfo).getId();
		return null != id ? new ResultVo(true, "创建Affiche成功", id) : new ResultVo(false, "创建Affiche失败", id);
	}
	
	/**
	 * 网站公告--查询单个
	 */
	@Override
	@Cacheable(value="slcf_affiche",key="#id", cacheManager="redis")
	public Map<String,Object> findOneAffiche(String id) throws SLException{
		AfficheInfoEntity afficheInfoOriginal =  afficheInfoRepository.findOne(id);
		AfficheInfoEntity afficheInfo = afficheInfoOriginal.cloneAfficheInfo(afficheInfoOriginal);
		String userCreate = userRepository.findUserNameById(afficheInfo.getCreateUser());
		afficheInfo.setCreateUser(null != userCreate ? userCreate:afficheInfo.getCreateUser());
		String userPublish = userRepository.findUserNameById(afficheInfo.getCreateUser());
		afficheInfo.setPublishUser(null != userPublish ? userPublish:afficheInfo.getPublishUser());
		return BeanMapConvertUtil.toMap(afficheInfo);
	}
	
	/**
	 * 网站公告--查询单个(替换img图片的src增加前缀http://image.shanlinbao.com)
	 * 
	 * @param id
	 * 	 		<li>id 	                              公告ID 	   {@link java.lang.String}</li>
	 * @return 
	 * 	 		<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 * @throws SLException
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_afficheSearch", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_afficheCount", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_affiche", key= "#id", cacheManager="redis") })
	public Map<String,Object> findOneAfficheReplaceImgSrc(String id) throws SLException{
		AfficheInfoEntity afficheInfoOriginal =  afficheInfoRepository.findOne(id);
		AfficheInfoEntity afficheInfo = afficheInfoOriginal.cloneAfficheInfo(afficheInfoOriginal);
		//img src 路径替换
		String replactHtml = ImgSrcReplaceUtil.getContent(afficheInfo.getAfficheContent());
		afficheInfo.setAfficheContent(replactHtml);
		return BeanMapConvertUtil.toMap(afficheInfo);  
	}
	
	/**
	 * 网站公告--修改
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_afficheSearch", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_afficheCount", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_affiche", key= "#afficheInfo.id", cacheManager="redis") })
	public ResultVo updateAffiche(AfficheInfoEntity afficheInfo) throws SLException{
		AfficheInfoEntity afficheInfoUpd = afficheInfoRepository.findOne(afficheInfo.getId());
		if (Constant.AFFICHE_STATUS_NEW.equals(afficheInfoUpd.getAfficheStatus())|| Constant.AFFICHE_STATUS_INVALIED.equals(afficheInfoUpd.getAfficheStatus())) {
			afficheInfo.setBasicModelProperty(afficheInfo.getLastUpdateUser(),false);
			if(!afficheInfoUpd.updateAffiche(afficheInfo))
				throw new SLException("编辑网站公告失败!");
			return new ResultVo(true);
		} else {
			return new ResultVo(false, "只有新建或者已失效的公告才可以编辑!");
		}
	}

	/**
	 * 网站公告--失效
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_afficheSearch", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_afficheCount", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_affiche", key= "#id", cacheManager="redis") })
	public ResultVo invalidAffiche(String id,String custId) throws SLException{
		AfficheInfoEntity afficheInfo = afficheInfoRepository.findOne(id);
		AfficheInfoEntity afficheInfoUpd = new AfficheInfoEntity();
		afficheInfo.setBasicModelProperty(custId,false);
		afficheInfoUpd.setAfficheStatus(Constant.AFFICHE_STATUS_INVALIED);
		if (!afficheInfo.updateAffiche(afficheInfoUpd))
			throw new SLException("失效网站公告失败!");
		return new ResultVo(true);
	}
	 
	/**
	 * 网站公告--发布
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_afficheSearch", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_afficheCount", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_affiche", key= "#id", cacheManager="redis") })
	public ResultVo publishAffiche(String id,String custId)throws SLException {
		AfficheInfoEntity afficheInfo = afficheInfoRepository.findOne(id);
		if(Constant.AFFICHE_STATUS_PUBLISHED.equals(afficheInfo.getAfficheStatus()))
			return new ResultVo(false,"公告已经发布,请不要重复发布");
		String afficheType = afficheInfo.getAfficheType();
		int num = afficheInfoRepository.countByAfficheTypeAndAfficheStatus(afficheType, Constant.AFFICHE_STATUS_PUBLISHED);
//		if (num > 0 && Constant.AFFICHE_TYPE_ALL.equals(afficheType))
//			return new ResultVo(false, "已发布的网站公告数据已经存在!");
		if (num >= 5 && Constant.AFFICHE_TYPE_BANNER.equals(afficheType))
			return new ResultVo(false, "已发布的banner数据超限!");
		AfficheInfoEntity afficheInfoUpd = new AfficheInfoEntity();
		afficheInfoUpd.setAfficheStatus(Constant.AFFICHE_STATUS_PUBLISHED);
		afficheInfoUpd.setPublishUser(custId);
		afficheInfoUpd.setPublishTime(DateTime.now().toDate());
		afficheInfoUpd.setBasicModelProperty(custId, false);
		if (!afficheInfo.updateAffiche(afficheInfoUpd))
			throw new SLException("发布网站公告失败!");
		/** 插入消息记录 **/ // 类型"网站通知"和"通知-业务员"插入到消息表中
		// update by lyy @2016/11/14 to add salesMan's Type --Start
		if (Constant.AFFICHE_TYPE_NOTICE.equals(afficheType) || Constant.AFFICHE_TYPE_SALESMAN.equals(afficheType)) {
		// update by lyy @2016/11/14 to add salesMan's Type --End
			SystemMessageInfoEntity sysMessage = new SystemMessageInfoEntity(afficheInfo.getAfficheTitle(),afficheInfo.getAfficheContent());
			sysMessage.setBasicModelProperty(custId, true);
			systemMessageService.saveSiteMessage(sysMessage, afficheType);
		}
		return new ResultVo(true);
	}
	

	/**
	 * 网站公告--删除
	 */
	@Override
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Caching(evict = { @CacheEvict(value = "slcf_afficheSearch", allEntries = true, cacheManager="redis"), 
			@CacheEvict(value = "slcf_afficheCount", allEntries = true, cacheManager="redis"),
			@CacheEvict(value = "slcf_affiche", key= "#id", cacheManager="redis") })
	public ResultVo deleteAffiche(String id,String custId) throws SLException{
		AfficheInfoEntity afficheInfo = afficheInfoRepository.findOne(id);
		AfficheInfoEntity afficheInfoUpd = new AfficheInfoEntity();
		afficheInfo.setBasicModelProperty(custId,false);
		afficheInfoUpd.setAfficheStatus(Constant.AFFICHE_STATUS_DELETED);
		if (!afficheInfo.updateAffiche(afficheInfoUpd))
			throw new SLException("删除网站公告失败!");
		return new ResultVo(true);
	}

	/**
	 * 网站公告--网站最新公告查询	
	 */
	@Override
	@Cacheable(value="slcf_afficheSearch",key="'findNewestWebsiteAffiche'", cacheManager="redis")
	public Map<String,Object> findNewestWebsiteAffiche() throws SLException {
		List<AfficheInfoEntity> list = afficheInfoRepository.findByAfficheTypeAndAfficheStatusOrderByPublishTimeDesc(Constant.AFFICHE_TYPE_ALL, Constant.AFFICHE_STATUS_PUBLISHED);
		if( null != list && list.size() > 0 )
			return BeanMapConvertUtil.toMap(list.get(0));
		return Maps.newHashMap();
	}
	
	/**
	 * 网站公告--查询行业动态、媒体报道
	 */
	public List<Map<String,Object>> findAffiche() throws SLException{
		return BeanMapConvertUtil.beanToMapList(afficheInfoRepository.findTop5ByAfficheTypeInAndAfficheStatusOrderByPublishTimeDesc(Arrays.asList(Constant.AFFICHE_TYPE_NEWS,Constant.AFFICHE_TYPE_MEDIA), Constant.AFFICHE_STATUS_PUBLISHED));
	}

	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	@CacheEvict(value = "slcf_afficheCount", key = "'countAffiche_' + #params['custId']", cacheManager="redis")
	public ResultVo readAffiche(Map<String, Object> params) throws SLException {
		CustInfoEntity custInfoEntity = custInfoRepository.findOne((String)params.get("custId"));
		AfficheInfoEntity afficheInfoEntity = afficheInfoRepository.findOne((String)params.get("id"));
		if(custInfoEntity != null && afficheInfoEntity != null) {
			SystemMessageReadInfoEntity systemMessageReadInfoEntity = new SystemMessageReadInfoEntity();
			SystemMessageInfoEntity message = new SystemMessageInfoEntity();
			message.setId("0");
			systemMessageReadInfoEntity.setMessage(message);
			systemMessageReadInfoEntity.setRelateType(Constant.TABLE_BAO_T_AFFICHE_INFO);
			systemMessageReadInfoEntity.setRelatePrimary(afficheInfoEntity.getId());
			systemMessageReadInfoEntity.setReceiveCust(custInfoEntity);
			systemMessageReadInfoEntity.setIsRead(Constant.SITE_MESSAGE_ISREAD);
			systemMessageReadInfoEntity.setReadDate(new Date());
			systemMessageReadInfoEntity.setBasicModelProperty((String)params.get("custId"), true);
			systemMessageReadInfoRepository.save(systemMessageReadInfoEntity);
		}
		
		return new ResultVo(true);
	}

	@Override
	@Cacheable(value="slcf_afficheCount",key="'countAffiche_' + #params['custId']",  condition="#params['custId']!=null", cacheManager="redis")
	public Map<String, Object> countAffiche(Map<String, Object> params)
			throws SLException {
		
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuilder sqlString = new StringBuilder()
		.append(" select count(1) \"allAffiche\" ")
		.append(" from BAO_T_AFFICHE_INFO t ")
		.append(" where t.affiche_type = '网站公告' and t.affiche_status = '已发布' ")
		.append(" union all ")
		.append(" select count(1) \"allAffiche\" ")
		.append(" from BAO_T_AFFICHE_INFO t, BAO_T_SYSTEM_MESSAGE_READ_INFO s ")
		.append(" where t.id = s.relate_primary ");
		
		List<Object> objList=new ArrayList<>(); 
		if (StringUtils.isNotEmpty((String)params.get("custId"))){
			sqlString.append(" and s.receive_cust_id = ? ");
			objList.add((String)params.get("custId"));
		}
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
		if(list != null && list.size() > 0) {
			int allAfficheNum = Integer.valueOf(list.get(0).get("allAffiche").toString());
			int readAfficheNum = Integer.valueOf(list.get(1).get("allAffiche").toString());
			result.put("allAfficheNum", allAfficheNum);
			result.put("readAfficheNum", readAfficheNum);
			result.put("unReadAfficheNum", allAfficheNum - readAfficheNum);
		}
		else {
			result.put("allAfficheNum", 0);
			result.put("readAfficheNum", 0);
			result.put("unReadAfficheNum", 0);
		}
		return result;
	}
	
}
