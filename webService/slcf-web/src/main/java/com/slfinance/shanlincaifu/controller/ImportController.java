package com.slfinance.shanlincaifu.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**
 * 导入文件控制层
 *
 */
@Slf4j
@Controller
@RequestMapping(value="/attachment", produces="application/json;charset=UTF-8")
public class ImportController {
	
	@Value(value = "${upload.user.path}")
	private String uploadUserPath;
	
	/**
	 * 上传文件的最大文件大小10M
	 */
	private static final long MAX_FILE_SIZE = 1024 * 1024 * 10;
	
	/**图片后缀集合**/
	private static final List<String> imageSuffix = Arrays.asList("bmp","jpg","jpeg","png","gif");
	
	@Autowired
	private CustomerService customerService;
	
	/**
	 * 上传附件
	 * 
	 * @param file
	 * @param request
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/uploadFile", method = RequestMethod.POST)
	public @ResponseBody ResultVo uploadFile(MultipartFile file,MultipartHttpServletRequest request)throws SLException{
		
		Map<String,String[]> requestParams = request.getParameterMap();
		// 取普通参数
		String custId = requestParams.get("custId") != null && requestParams.get("custId").length > 0?requestParams.get("custId")[0]:"";
		String tradeType = requestParams.get("tradeType") != null && requestParams.get("tradeType").length > 0?requestParams.get("tradeType")[0]:"";
		// 过滤参数中的引号
		custId = Strings.nullToEmpty(custId).replaceAll("\"", "").replaceAll("\"", "");
		tradeType = Strings.nullToEmpty(tradeType).replaceAll("\"", "").replaceAll("\"", "");
		
		if(StringUtils.isEmpty(custId)) {
			return new ResultVo(false, "客户ID不能为空");
		}
		
		if(StringUtils.isEmpty(tradeType)) {
			return new ResultVo(false, "业务类型不能为空");
		}
		
		Map<String, String> attachmentTypeMap = getAttachmentType(tradeType);
		if(StringUtils.isEmpty(attachmentTypeMap.get("attachmentType"))) {
			return new ResultVo(false, "业务类型不正确");
		}
		
		CustInfoEntity custInfoEntity = customerService.findByCustId(custId);
		if(custInfoEntity == null) {
			return new ResultVo(false, "用户不存在");
		}
		
		// 生成图片路径
		String storagePath = custId + attachmentTypeMap.get("uploadFolder");
		List<Map<String, Object>> attachmentList = new ArrayList<Map<String, Object>>();
		MultiValueMap<String,MultipartFile> map = request.getMultiFileMap();
		for( String key : map.keySet() ) {
			for(MultipartFile multipartFile :  map.get(key) ){
				
				/**1:校验图片名称、后缀、大小**/
				String fileName = multipartFile.getOriginalFilename();
				String[] imageNameAndSuffix  = fileName.split("[.]");
				if( imageNameAndSuffix ==null || ( imageNameAndSuffix != null && imageNameAndSuffix.length != 2 ) )
					return new ResultVo(false,"图片非法");
				boolean ifSuffixContains = imageSuffix.contains(imageNameAndSuffix[1]);
				if(!ifSuffixContains)
					return new ResultVo(false,"图片附件格式非法,目前只支持bmp、jpg、jpeg、png、gif格式");
				
				/**是否包含特殊字符**/
				if (fileName.indexOf(",") != -1) 
					return new ResultVo(false, "上传文件名称存在特殊字符");
				
				/**校验大小**/
				if (multipartFile.getSize() > MAX_FILE_SIZE) 
					return new ResultVo(false,"图片大小超限"); 
				
				/**2:组装需要返回的数据**/
				Map<String, Object> attachment  = Maps.newConcurrentMap();
				attachment.put("storagePath", storagePath);
				attachment.put("docType", "PIG");
				attachment.put("attachmentName", UUID.randomUUID()+"."+imageNameAndSuffix[1]);
				attachment.put("attachmentType", getRealAttachmentType(attachmentTypeMap.get("attachmentType"), imageNameAndSuffix[0]));
				attachmentList.add(attachment);
				/**3:分别上传图片**/
				uploadFile(multipartFile, attachment.get("attachmentName").toString(), storagePath);
			}
		}
		
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("attachmentList", attachmentList);
		
		return new ResultVo(true, "上传附件成功", result);
	}
	
	/**
	 * 上传文件
	 * 
	 * @param multipartFile
	 * @return
	 */
	protected String uploadFile(MultipartFile multipartFile, String uploadFileName,String uploadFilePath) throws SLException {
		//根路劲下的路径
		String uploadPath = uploadUserPath + "/" + uploadFilePath;
		// 如果路径不存在,则创建
		mkDir(uploadPath);
		
		String fullFileName = uploadPath +"/"+uploadFileName;
		try {
			FileCopyUtils.copy(multipartFile.getBytes(), new FileOutputStream(fullFileName));
		} catch (FileNotFoundException e) {
			log.info(e.getMessage() != null? e.getMessage():e.getCause().toString());
			throw new SLException("上传失败， 请重新上传！");
		} catch (IOException e) {
			log.info(e.getMessage() != null? e.getMessage():e.getCause().toString());
			throw new SLException("上传失败， 请重新上传！");
		}

		return fullFileName;
	}

	/**
	 * 创建文件目录
	 * @param pathName
	 */
	private static void mkDir(String pathName) {
		File file = new File(pathName);
		if (file.exists()) {
			return;
		}
		if (file.mkdir()) {
			file.setExecutable(true, false);
			file.setReadable(true, false);
			file.setWritable(true, false);
			return;
		}
		File canonFile = null;
		try {
			canonFile = file.getCanonicalFile();
		} catch (IOException e) {
			log.info(e.getMessage() != null? e.getMessage():e.getCause().toString());
			return;
		}
		File parent = canonFile.getParentFile();
		mkDir(parent.getPath());
		mkDir(canonFile.getPath());
	}	
	
	
	/**
	 * 获取附件类型
	 * 
	 * @param tradeType
	 * @return
	 */
	private Map<String, String> getAttachmentType(String tradeType) {
		Map<String, String> result = Maps.newConcurrentMap(); 
		switch(tradeType) {
		case Constant.OPERATION_TYPE_42: // 线下充值
			result.put("attachmentType", Constant.ATTACHMENT_TYPE_02);
			result.put("uploadFolder", "/recharge");
			break;
		case Constant.OPERATION_TYPE_44: // 附属银行卡申请
			result.put("attachmentType", Constant.ATTACHMENT_TYPE_03);
			result.put("uploadFolder", "/offlinebank");
			break;
		}
		return result;
	}
	
	private String getRealAttachmentType(String attachmentType, String fileName) {
		if(Constant.ATTACHMENT_TYPE_03.equals(attachmentType)) { // 附属银行卡申请，判断文件名称是否存在
			if(Constant.ATTACHMENT_TYPE_BANK_LIST.contains(fileName)) { // 文件名称为附件类型
				attachmentType = fileName;
			}
		}
		return attachmentType;
	}
}
