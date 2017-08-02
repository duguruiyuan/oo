package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.InfoDocumentEntity;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.InfoDocumentRepository;
import com.slfinance.shanlincaifu.repository.custom.InfoDocumentRepositoryCustom;
import com.slfinance.shanlincaifu.service.LawsAndRegulationsService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

@Service
public class LawsAndRegulationsServiceImpl implements LawsAndRegulationsService {
	@Autowired
	private InfoDocumentRepository infoDocumentRepository;
	@Autowired
	private AttachmentRepository attachmentRepository;
	@Autowired
	private InfoDocumentRepositoryCustom infoDocumentRepositoryCustom;

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo createLawsAndRegulations(Map<String, Object> params)
			throws SLException {

		String title = CommonUtils.emptyToString(params.get("title"));// 标题
		String content = CommonUtils.emptyToString(params.get("content"));// 内容
		String lawsFile = CommonUtils.emptyToString(params.get("lawsFile"));// 文件路劲
		String custId = CommonUtils.emptyToString(params.get("custId"));// 操作人
		String fileName = CommonUtils.emptyToString(params.get("fileName"));// 文件名称
		String linkAddress = CommonUtils.emptyToString(params.get("linkAddress"));//网址
		if (StringUtils.isEmpty(title)) {
			return new ResultVo(false, "标题不能为空！");
		}
		if (StringUtils.isEmpty(content)) {
			return new ResultVo(false, "内容不能为空！");
		}
		if(!StringUtils.isEmpty(lawsFile)&&!StringUtils.isEmpty(linkAddress)){
			return new ResultVo(false, "附件与网址只能上传一种！");
		}
		
		/** 插入报告记录 **/
		InfoDocumentEntity infoDocument = new InfoDocumentEntity();
		infoDocument.setTitle(title);
		infoDocument.setIssueStatus(Constant.RELEASE_STATUS01);
		infoDocument.setContent(content);
		infoDocumentRepository.save(infoDocument);

		/** 插入附件信息 **/
		AttachmentInfoEntity fileAttachment = new AttachmentInfoEntity();
		fileAttachment.setAttachmentName(fileName);
		fileAttachment.setAttachmentType("法律法规");
		fileAttachment.setRelatePrimary(infoDocument.getId());
		if(!StringUtils.isEmpty(lawsFile)&&StringUtils.isEmpty(linkAddress)){
			fileAttachment.setStoragePath(lawsFile);
			fileAttachment.setDocType("PDF");
		}
		if(!StringUtils.isEmpty(linkAddress)&&StringUtils.isEmpty(lawsFile)){
			if(!linkAddress.startsWith("http://")){
				linkAddress = "http://" + linkAddress;
			}
			fileAttachment.setStoragePath(linkAddress);
			fileAttachment.setDocType("URL");
		}
		fileAttachment.setRelateType(Constant.TABLE_BAO_T_INFO_DOCUMENT);
		fileAttachment.setBasicModelProperty(custId, true);
		attachmentRepository.save(fileAttachment);

		return new ResultVo(true, "创建成功");
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateReleaseStatus(Map<String, Object> params)
			throws SLException {
		String lawsAndRegulationsId = CommonUtils.emptyToString(params
				.get("lawsAndRegulationsId"));// 法律法规ID
		String title = CommonUtils.emptyToString(params.get("title"));// 标题
		String content = CommonUtils.emptyToString(params.get("content"));// 内容
		String lawsFile = CommonUtils.emptyToString(params.get("lawsFile"));// 文件路径
		String custId = CommonUtils.emptyToString(params.get("custId"));// 操作人
		String fileName = CommonUtils.emptyToString(params.get("fileName"));// 文件名称
		String linkAddress = CommonUtils.emptyToString(params.get("linkAddress"));//网址
		if(!StringUtils.isEmpty(lawsFile)&&!StringUtils.isEmpty(linkAddress)){
			return new ResultVo(false, "附件与网址只能上传一种！");
		}
		InfoDocumentEntity infoDocument = infoDocumentRepository
				.findOne(lawsAndRegulationsId);
		AttachmentInfoEntity fileAttachment = attachmentRepository
				.findByRelatePrimary(lawsAndRegulationsId);
		String message = "操作失败";
		switch ((String) params.get("releaseStatus")) {
		case Constant.RELEASE_STATUS_RELEASE:
			infoDocument.setIssueStatus(Constant.RELEASE_STATUS02);
			message = "发布操作成功";
			break;
		case Constant.RELEASE_STATUS_FAILURE:
			infoDocument.setIssueStatus(Constant.RELEASE_STATUS03);
			message = "失效操作成功";
			break;
		case Constant.RELEASE_STATUS_ALTER:
			if (!StringUtils.isEmpty(title)) {
				infoDocument.setTitle(title);
			}
			if (!StringUtils.isEmpty(content)) {
				infoDocument.setContent(content);
			}
			if (!StringUtils.isEmpty(fileName)) {
				fileAttachment.setAttachmentName(fileName);
			}
			if(!StringUtils.isEmpty(lawsFile)&&StringUtils.isEmpty(linkAddress)){
				fileAttachment.setStoragePath(lawsFile);
				fileAttachment.setDocType("PDF");
			}
			if(!StringUtils.isEmpty(linkAddress)&&StringUtils.isEmpty(lawsFile)){
				if(!linkAddress.startsWith("http://")){
					linkAddress = "http://" + linkAddress;
				}
				fileAttachment.setStoragePath(linkAddress);
				fileAttachment.setDocType("URL");
			}
			
			message = "修改成功";

		default:
			break;
		}
		infoDocument.setBasicModelProperty(custId, false);
		fileAttachment.setBasicModelProperty(custId, false);
		return new ResultVo(true, message);
	}

	@Override
	public Map<String, Object> findAllLawsAndRegulationsList(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = infoDocumentRepositoryCustom
				.findAllLawsAndRegulationsList(params);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}
	
	@Override
	public Map<String, Object> backFindAllLawsAndRegulationsList(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = infoDocumentRepositoryCustom
				.backFindAllLawsAndRegulationsList(params);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findBylawsAndRegulationsId(
			Map<String, Object> params) {
		return infoDocumentRepositoryCustom.findBylawsAndRegulationsId(params);
	}

}
