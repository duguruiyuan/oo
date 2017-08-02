/** 
 * @(#)APPBankCardController.java 1.0.0 2015年7月20日 上午8:56:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller.mobile;

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

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.mobile.APPBankService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 手机APP端用户登陆模块功能控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年7月20日 上午8:56:04 $ 
 */
@Slf4j
@Controller
@RequestMapping(value="/bank", produces="application/json;charset=UTF-8")
public class APPBankCardController {

	@Value(value = "${upload.user.path}")
	private String uploadUserPath;
	
	protected String uploadFolder = "/unbindCardFile";
	
	private static final String lossProveImageName ="1",bankCardFile="2", cardFrontFile="3",cardReverseFile="4";

	/**
	 * 上传文件的最大文件大小10M
	 */
	private static final long MAX_FILE_SIZE = 1024 * 1024 * 10;
	
	@Autowired
	private APPBankService  appBankService;
	
	/**
	 * 银行卡列表
	 */
	@RequestMapping(value="/bankCardList", method = RequestMethod.POST)
	public @ResponseBody ResultVo bankCardList(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appBankService.getBankCardList(paramsMap);
	}
	
	/**
	 * 解绑银行卡申请提交
	 * @throws FileUploadException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/unBindBankCard", method = RequestMethod.POST)
	public @ResponseBody ResultVo unBindBankCard(MultipartFile file,MultipartHttpServletRequest request)throws SLException{
		
		/**校验上传图片**/
		ResultVo checkAndUploadResult = checkImageFile(file,request);
		if(!ResultVo.isSuccess(checkAndUploadResult))
			return checkAndUploadResult;
		
		ResultVo unBindBankCardResult = appBankService.unBindBankCard((Map<String,Object>)checkAndUploadResult.getValue("data"));
		if(!ResultVo.isSuccess(unBindBankCardResult))
			return unBindBankCardResult;
		
		return new ResultVo(true);
	}
	
//---私有方法--------------------------------------------------------------------------------------------------------
	
	/**
	 * 1：检查图片是否是挂失证明(1)、银行卡图片(2)、身份证正面(3)、身份证反面(4)附件
	 * 2：替换附件名称和组装附件数据
	 * 3:上传附件
	 * @throws SLException 
	 */
	
	public ResultVo checkImageFile(MultipartFile file,MultipartHttpServletRequest request) throws SLException {
		/**图片名称集合**/
		List<String> imageName = Arrays.asList(lossProveImageName,bankCardFile, cardFrontFile,cardReverseFile);
		/**图片后缀集合**/
		List<String> imageSuffix = Arrays.asList("bmp","jpg","jpeg","png","gif");
		/**获取解绑参数信息**/
		Map<String,String[]> unBindInfoParams = request.getParameterMap();
//		if(unBindInfoParams == null || ( unBindInfoParams !=null && unBindInfoParams.size() != 6 ))
//			return new ResultVo(false,"解绑参数信息不完整");
		Map<String,Object> params = Maps.newConcurrentMap();
		params.put("custId", unBindInfoParams.get("custId") != null && unBindInfoParams.get("custId").length > 0?unBindInfoParams.get("custId")[0]:null);
		params.put("bankId", unBindInfoParams.get("bankId") != null && unBindInfoParams.get("bankId").length > 0?unBindInfoParams.get("bankId")[0]:null);
		params.put("tradePassword", unBindInfoParams.get("tradePassword") != null && unBindInfoParams.get("tradePassword").length > 0?unBindInfoParams.get("tradePassword")[0]:null);
		params.put("unbindType", unBindInfoParams.get("unbindType") != null && unBindInfoParams.get("unbindType").length > 0?unBindInfoParams.get("unbindType")[0]:null);
		params.put("unbindReason", unBindInfoParams.get("unbindReason") != null && unBindInfoParams.get("unbindReason").length > 0?unBindInfoParams.get("unbindReason")[0]:null);
		params.put("ipAddress", unBindInfoParams.get("ipAddress") != null && unBindInfoParams.get("ipAddress").length > 0?unBindInfoParams.get("ipAddress")[0]:null);
		
		params.put("custId", Strings.nullToEmpty((String)params.get("custId")).replaceAll("\"", ""));
		params.put("bankId", Strings.nullToEmpty((String)params.get("bankId")).replaceAll("\"", ""));
		params.put("tradePassword", Strings.nullToEmpty((String)params.get("tradePassword")).replaceAll("\"", ""));
		params.put("unbindType", Strings.nullToEmpty((String)params.get("unbindType")).replaceAll("\"", ""));
		params.put("unbindReason", Strings.nullToEmpty((String)params.get("unbindReason")).replaceAll("\"", ""));
		params.put("ipAddress", Strings.nullToEmpty((String)params.get("ipAddress")).replaceAll("\"", ""));
		
		/**图片数据库存储路径**/
		String storagePath = params.get("custId") + uploadFolder;
		List<Map<String, Object>> attachmentList = new ArrayList<Map<String, Object>>();
		
		MultiValueMap<String,MultipartFile> map = request.getMultiFileMap();
		for( String key : map.keySet() ) {
			for(MultipartFile multipartFile :  map.get(key) ){
				
				/**1:校验图片名称、后缀、大小**/
				String fileName = multipartFile.getOriginalFilename();
				String[] imageNameAndSuffix  = fileName.split("[.]");
				if( imageNameAndSuffix ==null || ( imageNameAndSuffix != null && imageNameAndSuffix.length != 2 ) )
					return new ResultVo(false,"图片非法");
				boolean ifNameContains = imageName.contains(imageNameAndSuffix[0]);
				if(!ifNameContains)
					return new ResultVo(false,"图片附件名称非法");
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
				String attachmentType  = "";
				switch (imageNameAndSuffix[0]) {
				/**挂失证明(1)**/
				case lossProveImageName:
					attachmentType = Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_LOST;
					break;
				/**银行卡图片(2)**/
				case bankCardFile:
					attachmentType = Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_CARD;
					break;
				/**身份证正面(3)**/
				case cardFrontFile:
					attachmentType = Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE;
					break;
				/**身份证反面(4)**/
				case cardReverseFile:
					attachmentType = Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND;
					break;
				default:
					break;
				}
				attachment.put("attachmentType", attachmentType);
				attachmentList.add(attachment);
				/**3:分别上传图片**/
				uploadFile(multipartFile, attachment.get("attachmentName").toString(),(String)params.get("custId"));
			}
		}
		params.put("attachmentList", attachmentList);
		return new ResultVo(true,"上传图片成功通过",params);
	}
	
	
	/**
	 * 上传文件
	 * 
	 * @param multipartFile
	 * @return
	 */
	protected String uploadFile(MultipartFile multipartFile, String uploadFileName,String custId) throws SLException {
		//根路劲下的路径
		String uploadPath = uploadUserPath + "/"+custId + uploadFolder;
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
	
	
}
