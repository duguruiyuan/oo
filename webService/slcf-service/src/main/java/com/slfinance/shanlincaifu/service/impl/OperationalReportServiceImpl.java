package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
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
import com.slfinance.shanlincaifu.service.OperationalReportService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;
@Service
public class OperationalReportServiceImpl implements OperationalReportService{
	@Autowired
	private InfoDocumentRepository infoDocumentRepository;
	@Autowired
	private AttachmentRepository attachmentRepository;
	@Autowired
	private InfoDocumentRepositoryCustom infoDocumentRepositoryCustom;
	
	
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public  ResultVo createReport(Map<String, Object> params)throws SLException{
		
		String reportName = CommonUtils.emptyToString(params.get("reportName"));//报告名称
		String reportCover = CommonUtils.emptyToString(params.get("reportCover"));//报告封面地址
		String reportFile = CommonUtils.emptyToString(params.get("reportFile"));//PDF文件地址
		String custId = CommonUtils.emptyToString(params.get("custId"));//操作人
		String coverName = CommonUtils.emptyToString(params.get("coverName"));//封面名称
		String fileName = CommonUtils.emptyToString(params.get("fileName"));//文件名称
		String reportTime = CommonUtils.emptyToString(params.get("reportTime"));//报告时间
		
		if(StringUtils.isEmpty(reportCover)){
			return new ResultVo(false,"封面图片未上传");
		}
		if(StringUtils.isEmpty(reportFile)){
			return new ResultVo(false,"报告文件未上传");
		}
		
		/** 插入报告记录**/
		InfoDocumentEntity infoDocument = new InfoDocumentEntity();
		infoDocument.setTitle(reportName);
		infoDocument.setReportTime(reportTime);
		infoDocument.setIssueStatus(Constant.RELEASE_STATUS01);
		infoDocumentRepository.save(infoDocument);
		
		/**插入附件信息 **/
		AttachmentInfoEntity coverAttachment = new AttachmentInfoEntity();
		coverAttachment.setStoragePath(reportCover);
		coverAttachment.setAttachmentName(coverName);
		coverAttachment.setDocType("PNG");
		coverAttachment.setRelateType(Constant.TABLE_BAO_T_INFO_DOCUMENT);
		coverAttachment.setRelatePrimary(infoDocument.getId());
		coverAttachment.setBasicModelProperty(custId, true);
		attachmentRepository.save(coverAttachment);
		/**插入附件信息 **/
		AttachmentInfoEntity fileAttachment = new AttachmentInfoEntity();
		fileAttachment.setStoragePath(reportFile);
		fileAttachment.setAttachmentName(fileName);
		fileAttachment.setDocType("PDF");;
		fileAttachment.setRelateType(Constant.TABLE_BAO_T_INFO_DOCUMENT);
		fileAttachment.setRelatePrimary(infoDocument.getId());
		fileAttachment.setBasicModelProperty(custId, true);
		attachmentRepository.save(fileAttachment);
		
		return new ResultVo(true,"创建成功");
		
	}

	@Override
	public Map<String, Object> backFindAllReportList(Map<String, Object> param){
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = infoDocumentRepositoryCustom.backFindAllReportList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateReleaseStatus(Map<String, Object> params)throws SLException {
		
		String reportId = CommonUtils.emptyToString(params.get("reportId")); //报告ID
		String reportTime = CommonUtils.emptyToString(params.get("reportTime"));
		String reportName = CommonUtils.emptyToString(params.get("reportName"));//报告名称
		String reportCover = CommonUtils.emptyToString(params.get("reportCover"));//报告封面地址
		String reportFile = CommonUtils.emptyToString(params.get("reportFile"));//PDF文件地址
		String coverName = CommonUtils.emptyToString(params.get("coverName"));//封面名称
		String fileName = CommonUtils.emptyToString(params.get("fileName"));//文件名称
		String custId = CommonUtils.emptyToString(params.get("custId"));//操作人
		InfoDocumentEntity infoDocument = infoDocumentRepository.findOne(reportId);
		AttachmentInfoEntity coverAttachment = attachmentRepository.findByRelatePrimaryAndDocType(reportId,"PNG");
		AttachmentInfoEntity fileAttachment =attachmentRepository.findByRelatePrimaryAndDocType(reportId, "PDF");
		String message="操作失败";
		switch ((String)params.get("releaseStatus")) {
		case Constant.RELEASE_STATUS_RELEASE:
			infoDocument.setIssueStatus(Constant.RELEASE_STATUS02);
			message ="发布操作成功";
			break;
		case Constant.RELEASE_STATUS_FAILURE:
			infoDocument.setIssueStatus(Constant.RELEASE_STATUS03);
			message ="失效操作成功";
			break;
		case Constant.RELEASE_STATUS_ALTER:	
			if(!StringUtils.isEmpty(reportName)){
				infoDocument.setTitle(reportName);
			}
			if(!StringUtils.isEmpty(reportTime)){
				infoDocument.setReportTime(reportTime);
			}
			if(!StringUtils.isEmpty(reportCover)){
				coverAttachment.setStoragePath(reportCover);
			}
			if(!StringUtils.isEmpty(reportFile)){
				fileAttachment.setStoragePath(reportFile);
			}
			if(!StringUtils.isEmpty(coverName)){
				coverAttachment.setAttachmentName(coverName);
			}
			if(!StringUtils.isEmpty(fileName)){
				fileAttachment.setAttachmentName(fileName);
			}

			message ="修改成功";
			
		default:
			break;
		}
		infoDocument.setBasicModelProperty(custId, false);
		fileAttachment.setBasicModelProperty(custId, false);
		coverAttachment.setBasicModelProperty(custId, false);
		return new ResultVo(true,message);
	}

	@Override
	public Map<String, Object> frontFindAllReportList(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page =infoDocumentRepositoryCustom.frontFindAllReportList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findByReportId(Map<String, Object> param) {
		
		return infoDocumentRepositoryCustom.findByReportId(param);
	}

	@Override
	public List<Map<String, Object>> findAllReportTime(
			Map<String, Object> params) {
		return infoDocumentRepositoryCustom.findAllReportTime(params);
	}
}
