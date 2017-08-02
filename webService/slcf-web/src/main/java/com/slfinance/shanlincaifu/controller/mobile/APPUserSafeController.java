/** 
 * @(#)APPUserSafeController.java 1.0.0 2015年5月19日 上午8:56:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller.mobile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.fileupload.FileUploadException;
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
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.mobile.APPUserSafeService;
import com.slfinance.shanlincaifu.utils.ImgCompressUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 手机APP端用户登陆模块功能控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月19日 上午8:56:04 $ 
 */
@Slf4j
@Controller
@RequestMapping(value="/user", produces="application/json;charset=UTF-8")
public class APPUserSafeController {

	@Autowired
	private APPUserSafeService  appUserSafeService;
	
	@Autowired
	private CustomerService customerService;
	
	@Value(value = "${upload.user.path}")
	private String uploadUserPath;
	
	private static int portraitSize = 2097152;
	
	/**
	 * 获取用户信息
	 */
	@RequestMapping(value="/getUserInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo getUserInfo(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.getUserInfo(parasMap);
	}
	
	/**
	 * 验证密码
	 */
	@RequestMapping(value="/checkLoginPassword", method = RequestMethod.POST)
	public @ResponseBody ResultVo checkLoginPassword(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.checkLoginPassword(parasMap);
	}
	
	/**
	 * 用户登陆-返回该用户已绑定银行卡信息列表
	 */
	@RequestMapping(value="/loginMobile", method = RequestMethod.POST)
	public @ResponseBody ResultVo loginMobile(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.loginMobile(parasMap);
	}
	
	/**
	 * 记录用户登录日志
	 */
	@RequestMapping(value="/recordLoginLog", method = RequestMethod.POST)
	public @ResponseBody ResultVo recordLoginLog(@RequestBody Map<String,Object> parasMap)throws SLException{
		return customerService.recordLoginLog(parasMap);
	}
	
	/**
	 * 忘记密码-发送手机验证码
	 */
	@RequestMapping(value="/checkRegistAndSendSMS", method = RequestMethod.POST)
	public @ResponseBody ResultVo checkRegistAndSendSMS(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.checkRegistAndSendSMS(parasMap);
	}
	
	/**
	 * 忘记密码-验证码是否正确
	 */
	@RequestMapping(value="/checkCodeAndUserVerified", method = RequestMethod.POST)
	public @ResponseBody ResultVo checkCodeAndUserVerified(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.checkCodeAndUserVerified(parasMap);
	}
	
	/**
	 * 忘记密码-实名认证
	 */
	@RequestMapping(value="/validateIdentity", method = RequestMethod.POST)
	public @ResponseBody ResultVo validateIdentity(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.validateIdentity(parasMap);
	}
	
	/**
	 * 忘记密码-修改
	 */
	@RequestMapping(value="/resetPwd", method = RequestMethod.POST)
	public @ResponseBody ResultVo resetPwd(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.resetPwd(parasMap);
	}
	
	/**
	 * 忘记密码-修改(校验验证码)
	 */
	@RequestMapping(value="/resetPassWord", method = RequestMethod.POST)
	public @ResponseBody ResultVo resetPassWord(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.resetPassWord(parasMap);
	}
	
	/**
	 * 重设交易密码(校验验证码)
	 */
	@RequestMapping(value="/resetTradePassword", method = RequestMethod.POST)
	public @ResponseBody ResultVo resetTradePassword(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.resetTradePassword(parasMap);
	}
	
	/**
	 * 修改手机-修改绑定手机
	 */
	@RequestMapping(value="/validCode", method = RequestMethod.POST)
	public @ResponseBody ResultVo validCode(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.validCode(parasMap);
	}
	
	/**
	 * 验证码发送次数
	 */
	@RequestMapping(value="/codeSendCount", method = RequestMethod.POST)
	public @ResponseBody ResultVo codeSendCount(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.codeSendCount(parasMap);
	}
	
	/**
	 * 查询客户基本信息和联系人基本信息(善林财富二期)
	 */
	@RequestMapping(value="/getCustAndContactCustInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo getCustAndContactCustInfo(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.getCustAndContactCustInfo(parasMap);
	}
	/**
	 * 修改账户用户信息和联系人信息(善林财富二期)
	 */
	@RequestMapping(value="/postCustAndContactCustInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo postCustAndContactCustInfo(@RequestBody Map<String,Object> parasMap)throws SLException{
		return appUserSafeService.postCustAndContactCustInfo(parasMap);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/uploadCustomPhoto", method = RequestMethod.POST)
	public @ResponseBody ResultVo uploadCustomPhoto(MultipartFile file,MultipartHttpServletRequest request)throws SLException, FileUploadException{

		ResultVo resultVo = handleCustomPhoto(file, request);
		if(!ResultVo.isSuccess(resultVo)){
			return resultVo;
		}
		
		String custId = ((Map<String,Object>)resultVo.getValue("data")).get("custId").toString();
		String portraitPath = ((Map<String,Object>)resultVo.getValue("data")).get("storagePath").toString();
		
		resultVo = customerService.uploadCustomPhoto((Map<String,Object>)resultVo.getValue("data"));
		if(!ResultVo.isSuccess(resultVo)){
			return resultVo;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("storagePath", String.format("/user/viewCustomPhoto?custId=%s", custId));
		map.put("portraitPath", portraitPath);
		
		return new ResultVo(true, "上传头像成功", map);
		 
	}
	
	@RequestMapping(value = "/viewCustomPhoto", method = RequestMethod.GET)
	public void viewUserPortrait(String custId, HttpServletRequest request, HttpServletResponse response) {
		try {
			CustInfoEntity custInfoEntity = customerService.findByCustId(custId);
			if(custInfoEntity == null) {
				log.error(String.format("客户信息%s不存在", custId));
				return;
			}
			if(custInfoEntity.getPortraitPath() == null) {
				log.error(String.format("客户信息%s头像不存在", custId));
				return;
			}
			BufferedImage image = ImageIO.read(new File(uploadUserPath + "/" + custInfoEntity.getPortraitPath()));
			ImageIO.write(image, "JPEG", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ResultVo handleCustomPhoto(MultipartFile file,MultipartHttpServletRequest request) throws SLException {
		Map<String,Object> params = Maps.newConcurrentMap();
		
		Map<String,String[]> requestParams = request.getParameterMap();
		if(requestParams == null || requestParams.get("custId") == null || requestParams.get("custId").length == 0) {
			return new ResultVo(false, "客户编号不能为空");
		}
		params.put("custId", requestParams.get("custId")[0]);
		
		params.put("custId", Strings.nullToEmpty((String)params.get("custId")).replaceAll("\"", ""));

		MultiValueMap<String,MultipartFile> multiValueMap = request.getMultiFileMap();
		if(multiValueMap == null || multiValueMap.size() == 0) {
			return new ResultVo(false, "头像图片不能为空");
		}
		
		 for (String key : multiValueMap.keySet()) {
			 for(MultipartFile multipartFile :  multiValueMap.get(key) ){
				 /**1:校验图片名称、后缀、大小**/
					String fileName = multipartFile.getOriginalFilename();
					String[] imageNameAndSuffix  = fileName.split("[.]");
					if( imageNameAndSuffix ==null || ( imageNameAndSuffix != null && imageNameAndSuffix.length != 2 ) )
						return new ResultVo(false,"图片非法");
					if(!Arrays.asList("bmp", "jpg", "jpeg", "png", "gif", "icon").contains(imageNameAndSuffix[1]))
						return new ResultVo(false,"图片附件格式非法,目前只支持bmp、jpg、jpeg、png、gif、icon格式");
					
					/**是否包含特殊字符**/
					if (fileName.indexOf(",") != -1) 
						return new ResultVo(false, "上传文件名称存在特殊字符");
					
					/**校验大小**/
					if (multipartFile.getSize() > portraitSize) 
						return new ResultVo(false, String.format("文件大小错误，不应超过", "文件大小错误，不应超过" + portraitSize / 1024 / 1024 + "M")); 
					
					
					/**2:组装需要返回的数据**/
					String attachmentName = UUID.randomUUID()+"."+imageNameAndSuffix[1]; // 文件名称
					params.put("storagePath", params.get("custId") + "/" + attachmentName); // uploadUserPath + custId + 图片名称
					params.put("docType", "PIG");
					params.put("attachmentName", attachmentName);
					params.put("attachmentType", "用户头像");

					// 上传文件
					uploadFile(multipartFile, attachmentName, (String)params.get("custId"));
					
					// 压缩成2张图片
					try {
						String ext = attachmentName.substring(attachmentName.lastIndexOf(".") + 1);
						String prefix = attachmentName.substring(0, attachmentName.lastIndexOf("."));
						String uploadPath = uploadUserPath + "/"+ params.get("custId") + "/";
						new ImgCompressUtil(uploadPath + attachmentName, new File(uploadPath + prefix + "_55x55." + ext)).resize(55, 55);
						new ImgCompressUtil(uploadPath + attachmentName, new File(uploadPath + prefix + "_100x100." + ext)).resize(100, 100);
					} catch (IOException e) {
						log.error("压缩图片失败！" + e.getMessage());
						return new ResultVo(false, "压缩图片失败!");
					}
			 }
			 break;
		 }
		return new ResultVo(true, "上传图片成功通过", params);
	}
	
	/**
	 * 上传文件
	 * 
	 * @param multipartFile
	 * @return
	 */
	protected String uploadFile(MultipartFile multipartFile, String uploadFileName,String custId) throws SLException {
		//根路劲下的路径
		String uploadPath = uploadUserPath + "/"+custId;
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
     * 获取用户等级
     * @author mali
     * @date 2017-6-8 16:27:02
     *
     * @param paramsMap
     *      <tt>custId     :String:客户ID</tt><br>
     * @return ResultVo
     *      <tt>ranking     :int:级别</tt><br>
     *      <tt>isShow      :String:是否点亮（0.否 1.是）</tt><br>
     *
     */
	@RequestMapping(value = "getUserRanking", method = RequestMethod.POST)
    @ResponseBody
	public ResultVo getUserRanking(@RequestBody Map<String,Object> paramsMap) throws SLException {
        return appUserSafeService.getUserRanking(paramsMap);
    }

    /**
     * 获取用户等级详情
     *
     * @author mali
     * @date 2017-6-8 20:58:46
     *
     * @param paramsMap
     *      <tt>custId     :String:客户ID</tt><br>
     * @return ResultVo
     *      <tt>ranking     :int:级别</tt><br>
     *      <tt>isShow      :String:是否点亮（0.否 1.是）</tt><br>
     *
     */
	@RequestMapping(value = "getUserRankingDetail", method = RequestMethod.POST)
    @ResponseBody
	public ResultVo getUserRankingDetail(@RequestBody Map<String,Object> paramsMap) throws SLException {
        return appUserSafeService.getUserRankingDetail(paramsMap);
    }
}
