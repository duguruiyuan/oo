package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ParamEntity;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.custom.BizExtractDataCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanProjectRepositoryCustom;
import com.slfinance.shanlincaifu.service.BizExtractData;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("bizExtractData")
public class BizExtractDataImpl implements BizExtractData {

	@Autowired
	private BizExtractDataCustom bizExtractDataCustom;
	

	@Autowired
	private LoanProjectRepositoryCustom loanProjectRepositoryCustom;
	
	@Autowired
	ParamService paramService;
	
	@Autowired
	private ParamRepository paramRepository;
	
	@Autowired
	private EmailService emailService;

	@Override
	public ResultVo dailyDataSummary(Map<String, Object> params) {
		List<Map<String, Object>> list = bizExtractDataCustom.dailyDataSummary(params);
		Map<String, Object> data = null;
		if(list != null && list.size() > 0) {
			data = list.get(0);
		}
		/*
		 *  "日充值金额(元)", "日提现金额(元)", "日投资金额(元)", "日发布金额(元)", "日转让金额(元)"
		 * , "日新增注册用户数","当日新增投资用户数", "当日投资用户总数", "历史总用户数", "库存单数", "库存金额"
		 */
		StringBuilder content = new StringBuilder();
		if(data == null){
			content.append("警告：当日数据取得出错请联系技术人员！ ");
		} else {
			String date = DateUtils.formatDate(new Date(), "yyyy年MM月dd日") + "的数据：";
			content.append("<p style=\"font-size:16px;\">").append(date).append("</p>")
			.append(" <table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"3\" class=\"tablesline\"> ")
			.append(" 	<tbody><tr> ")
			.append(" 		<td align=\"center\">日充值金额(元)</td> ")
			.append(" 		<td align=\"center\">日提现金额(元)</td> ")
			.append(" 		<td align=\"center\">日投资金额(元)</td> ")
			.append(" 		<td align=\"center\">日发布金额(元)</td> ")
			.append(" 		<td align=\"center\">日转让金额(元)</td> ")
			.append(" 		<td align=\"center\">日新增注册用户数</td> ")
			.append(" 		<td align=\"center\">当日新增投资用户数</td> ")
			.append(" 		<td align=\"center\">当日投资用户总数</td> ")
			.append(" 		<td align=\"center\">历史总用户数</td> ")
			.append(" 		<td align=\"center\">库存单数</td> ")
			.append(" 		<td align=\"center\">库存金额</td> ")
			.append(" 	</tr> ")
			.append(" 	<tr>  ")
			.append(" 		<td align=\"right\">"+data.get("日充值金额(元)")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("日提现金额(元)")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("日投资金额(元)")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("日发布金额(元)")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("日转让金额(元)")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("日新增注册用户数")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("当日新增投资用户数")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("当日投资用户总数")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("历史总用户数")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("库存单数")+"</td> ")
			.append(" 		<td align=\"right\">"+data.get("库存金额")+"</td> ")
			.append(" 	</tr></tbody> ")
			.append(" </table> ")
			;
		}
		
		ParamEntity entity = paramRepository.findByTypeNameAndParameterName("善林财富设置", Constant.JOB_NAME_DAILY_DATA_SUMMARY);
		if(entity == null || StringUtils.isEmpty(entity.getValue())){
			return new ResultVo(false, "数据出错");
		}
		String[] emails = entity.getValue().trim().split(",");
		
		for (int i = 0; i < emails.length; i++) {
			String email = emails[i];
			if(StringUtils.isEmpty(email)){
				continue;
			}
			Map<String, Object> smsInfo = Maps.newHashMap();
			smsInfo.put("to", email);// 收件人邮箱地址
			smsInfo.put("type", MailType.HTML);
			smsInfo.put("title", "善林财富平台每日数据汇总");
			smsInfo.put("content", content.toString());
			smsInfo.put("ftlPath", Constant.BIND_MAIL_TEMPLATE);
			smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
			//		smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
			log.info("准备发送邮件给", smsInfo.get("to"));
			ResultVo sendEmailResult = null;
			try {
				sendEmailResult = emailService.sendEmail(smsInfo);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SLException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			} 
			
			log.info("发送邮件处理结果：", sendEmailResult.getValue("message"));
		}
		
		return new ResultVo(true, "操作成功");
	}

	/**
	 * 邮件发送平台每日推送数据汇总<br>
	 * @author guoyk
	 * @date 2017/4/25 14:57:30
	 * <tt>客户名称  手机号  投资日期  投资金额  投资状态  投资标的</tt><br>
	 */
	@Override
	public ResultVo dailyDataPropellingSummary(Map<String, Object> params) {
		//查询需要发送的数据
		List<Map<String, Object>> list = bizExtractDataCustom.dailyDataPropellingSummary(params);
		
		/*
		 *  "客户名称", "手机号", "投资日期", "投资金额", "投资状态", "投资标的"
		 */
		StringBuilder content = new StringBuilder();
		if(list == null || list.size() == 0){
			content.append("提示：当日无数据！ ");
		} else {
			
			
			String date = DateUtils.formatDate(new Date(), "yyyy年MM月dd日") + "的数据：";
			content.append("<p style=\"font-size:16px;\">").append(date).append("</p>")
			.append(" <table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"3\" class=\"tablesline\"> ")
			.append(" 	<tbody><tr> ")
			.append(" 		<td align=\"center\">客户名称</td> ")
			.append(" 		<td align=\"center\">手机号</td> ")
			.append(" 		<td align=\"center\">投资日期</td> ")
			.append(" 		<td align=\"center\">投资金额</td> ")
			.append(" 		<td align=\"center\">投资状态</td> ")
			.append(" 		<td align=\"center\">投资标的</td> ")
			.append(" 	</tr> ");
			
			for (Map<String, Object> data : list) {
				content.append(" 	<tr>  ")
				.append(" 		<td align=\"right\">"+data.get("客户名称")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("手机号")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("投资日期")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("投资金额")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("投资状态")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("投资标的")+"</td> ")
				.append(" 	</tr> ");
			}
			content.append(" </tbody></table> ")
			;
		}
		
		ParamEntity entity = paramRepository.findByTypeNameAndParameterName("善林财富设置", Constant.JOB_NAME_DAILY_PUSH_DATA_SUMMARY);
		if(entity == null || StringUtils.isEmpty(entity.getValue())){
			return new ResultVo(false, "数据出错");
		}
		String[] emails = entity.getValue().trim().split(",");
		for (int i = 0; i < emails.length; i++) {
			String email = emails[i];
			if(StringUtils.isEmpty(email)){
				continue;
			}
			Map<String, Object> smsInfo = Maps.newHashMap();
			smsInfo.put("to", email);// 收件人邮箱地址
			smsInfo.put("type", MailType.HTML);
			smsInfo.put("title", "善林财富平台每日推送数据汇总");
			smsInfo.put("content", content.toString());
			smsInfo.put("ftlPath", Constant.BIND_MAIL_TEMPLATE);
			smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
			//		smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
			log.info("准备发送邮件给", smsInfo.get("to"));
			ResultVo sendEmailResult = null;
			try {
				sendEmailResult = emailService.sendEmail(smsInfo);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SLException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			} 
			
			log.info("发送邮件处理结果：", sendEmailResult.getValue("message"));
		}
		
		return new ResultVo(true, "操作成功");
	
	}

	@Override
	public ResultVo dailyDataYZloanAccountSummary(
			Map<String, Object> params) {
		// 获取当前时间前一天 格式：年-月-日
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		// 实际放款时间
		String grantDate = DateUtils.formatDate(calendar.getTime(), "yyyy-MM-dd");
		params.put("grantDate", grantDate);
        //查询需要发送的数据
		List<Map<String, Object>> list =bizExtractDataCustom.dailyDataYZloanAccountSummary(params);
		StringBuilder content = new StringBuilder();
		if(list == null || list.size() == 0){
			content.append("提示：当日无数据！ ");
		} else {
			// 放款总金额
			BigDecimal totalGrantAmount = new BigDecimal("0");
			StringBuilder contentDetail = new StringBuilder()
	     	.append(" <table width=\"100%\" border=\"1\" cellspacing=\"1\" cellpadding=\"3\" class=\"tablesline\"> ")
			.append(" 	<tbody><tr> ")
			.append(" 		<td align=\"center\">借款编号</td> ")
			.append(" 		<td align=\"center\">公司名称</td> ")
			.append(" 		<td align=\"center\">借款金额</td> ")
			.append(" 		<td align=\"center\">放款状态</td> ")
			.append(" 		<td align=\"center\">放款时间</td> ")
			.append(" 		<td align=\"center\">推标时间</td> ")
			.append(" 	</tr> ");
			for (Map<String, Object> data : list) {
				/*
				 *  "借款编号", "公司名称", "借款金额", "放款状态", "放款时间", "推标时间"
				 */
				contentDetail.append("<tr>  ")
				.append(" 		<td align=\"right\">"+data.get("借款编号")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("公司名称")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("借款金额")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("放款状态")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("放款时间")+"</td> ")
				.append(" 		<td align=\"right\">"+data.get("推标时间")+"</td> ")
				.append(" 	</tr> ");
				totalGrantAmount = ArithUtil.add(totalGrantAmount, (BigDecimal)data.get("借款金额"));
			}
			contentDetail.append(" </tbody></table> ");
			String date1 = DateUtils.formatDate(calendar.getTime(), "yyyy年MM月dd日") + "的放款总金额为：";
			content.append("<p style=\"font-size:16px;\">").append(date1).append(totalGrantAmount).append("元 </p>")
			.append(contentDetail.toString());
		}
		ParamEntity entity = paramRepository.findByTypeNameAndParameterName("善林财富设置", Constant.JOB_NAME_DAILY_DATA_GRANT_AMOUNT_YZ);
		if(entity == null || StringUtils.isEmpty(entity.getValue())){
			return new ResultVo(false, "数据出错");
		}
		String[] emails = entity.getValue().trim().split(",");
		for (int i = 0; i < emails.length; i++) {
			String email = emails[i];
			if(StringUtils.isEmpty(email)){
				continue;
			}
			Map<String, Object> smsInfo = Maps.newHashMap();
			smsInfo.put("to", email);// 收件人邮箱地址
			smsInfo.put("type", MailType.HTML);
			smsInfo.put("title", "善林财富平台-意真金融每日放款金额数据汇总");
			smsInfo.put("content", content.toString());
			smsInfo.put("ftlPath", Constant.BIND_MAIL_TEMPLATE);
			smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
//			smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
			log.info("准备发送邮件给:{}", smsInfo.get("to"));
			ResultVo sendEmailResult = null;
			try {
				sendEmailResult = emailService.sendEmail(smsInfo);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SLException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			} 
			
			log.info("发送邮件处理结果：", sendEmailResult.getValue("message"));
		}
		
		return new ResultVo(true, "操作成功");
	}
	
}
