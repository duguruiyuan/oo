package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.repository.PosInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.WaitingAuditSendMessageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("waitingAuditSendMessageService")
public class WaitingAuditSendMessageServiceImpl implements WaitingAuditSendMessageService {

	@Autowired
	private StringRedisTemplate redisTemplate2;
	
	@Autowired
	private TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	ParamService paramService;
	
	@Autowired
	PosInfoRepository posInfoRepository;
	
	final String INIT_TIME = "2016-01-01 00:00:00";
	
	final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	//	public static void main(String[] args) {
	//	Date date = DateUtils.parseDate("2016-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
	//	System.out.println(DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss"));
	//	System.out.println(date.toString());
	//}
	
	// DateUtils.parseDate("2016-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
	// DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")
	/**
	 * 线下充值、附属银行卡，有待审核的数据
	 */
	@Override
	public void waitingAuditSendMessage() {
		String waitingAuditDate = redisTemplate2.opsForValue().get(Constant.WAITING_AUDIT_DATE);// Constant.WAITING_AUDIT_DATE
		
		// 判断waitingAuditDate是否为空
		if(StringUtils.isEmpty(waitingAuditDate) || null == DateUtils.parseDate(waitingAuditDate, DATE_FORMAT)) {// 判断时间格式是否正确
			// 第一次执行设置waitingAuditDate为2016-01-01 00:00:00，确保把所有待审核数据取出
			waitingAuditDate = INIT_TIME;
			redisTemplate2.opsForValue().set(Constant.WAITING_AUDIT_DATE, waitingAuditDate);
		}
		
		Date lastDate = DateUtils.parseDate(waitingAuditDate, DATE_FORMAT);
		// 获取当前时间now
		Date nowDate = new Date();
		/* 
		 * 线下充值、附属银行卡，有待审核的数据
		 * 取大于waitingAuditDate且小于等于now的所有待审核数据条数
		 */
		// 线下充值有待审核的数据
		int offLineRechargeCount = tradeFlowInfoRepositoryCustom.countOffLineRechargeData(lastDate, nowDate);
		// 附属银行卡有待审核的数据
		int offLineCardCount = tradeFlowInfoRepositoryCustom.countOffLineCardData(lastDate, nowDate);
		int sum = offLineRechargeCount + offLineCardCount;
		/* 
		 * 如果有新数据，把以前所有数据都发出去 
		 * 线下充值、附属银行卡，有待审核的数据时 >>>>>> 发送邮件
		 */
		if(sum > 0){
			Date initDate = DateUtils.parseDate(INIT_TIME, DATE_FORMAT);
			// 线下充值有待审核的数据
			int offLineRechargeCountAll = tradeFlowInfoRepositoryCustom.countOffLineRechargeData(initDate, nowDate);
			// 附属银行卡有待审核的数据
			int offLineCardCountAll = tradeFlowInfoRepositoryCustom.countOffLineCardData(initDate, nowDate);
			int sumAll = offLineRechargeCountAll + offLineCardCountAll;
			// 发送邮件
			ResultVo sendEmailResult = null;
			try {
				String email = paramService.findMonitorEmail("线下待审核监控邮箱");
				
				Map<String, Object> smsInfo = Maps.newHashMap();
				smsInfo.put("to", email); // 收件人邮箱地址 chenyun@shanlinjinrong.com
				smsInfo.put("type", MailType.TEXT);
				smsInfo.put("title", "线下充值、附属银行卡，有待审核的数据");
				smsInfo.put("content", "您有" + sumAll + "笔待审核记录需要审核，请登录 http://172.16.100.141:8000/web/index 进行处理。");
				smsInfo.put("ftlPath", Constant.BIND_MAIL_TEMPLATE);
				
				log.info("准备发送邮件给", smsInfo.get("to"));
				sendEmailResult = emailService.sendEmail(smsInfo);
				log.info("发送邮件处理结果：", sendEmailResult.getValue("message"));
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			if (!ResultVo.isSuccess(sendEmailResult)) {
				log.error("发送邮件失败");
				return; // 发送失败不更新
			}
		}
		// 设置Redis的Key为WAITING_AUDIT_DAT的值为now
		waitingAuditDate = DateUtils.formatDate(nowDate, DATE_FORMAT);
		redisTemplate2.opsForValue().set(Constant.WAITING_AUDIT_DATE, waitingAuditDate);
	}


	/**
	 * 监控一个POS单线上线下同时使用
	 */
	@Override
	public void monitorPosOnOnlineAndOffline() {
//		String monitorDate = redisTemplate2.opsForValue().get(Constant.MONITOR_DATE);// Constant.MONITOR_DATE
//		
//		// 判断waitingAuditDate是否为空
//		if(StringUtils.isEmpty(monitorDate) || null == DateUtils.parseDate(monitorDate, DATE_FORMAT)) {// 判断时间格式是否正确
//			// 第一次执行设置waitingAuditDate为2016-01-01 00:00:00，确保把所有待审核数据取出
//			monitorDate = INIT_TIME;
//			redisTemplate2.opsForValue().set(Constant.MONITOR_DATE, monitorDate);
//		}
//		
//		Date lastDate = DateUtils.parseDate(monitorDate, DATE_FORMAT);
//		// 获取当前时间now
//		Date nowDate = new Date();
		/* 
		 * 监控一个POS单线上线下同时使用
		 * 线下理财系统FT_T_INVEST_EXPAND_INFO表中参考号与善林财富系统中的参考号做对比，
		 * 若有相同，则发送邮件给 zhuyishan@shanlinjinrong.com
		 */
//		List<String> list = posInfoRepository.countMonitorPosData(lastDate, nowDate);
		List<String> list = posInfoRepository.countMonitorPosData();
		/* 
		 * 存在相同的参考号时 >>>>>> 发送邮件
		 * 如果有新数据，把以前所有数据都发出去
		 */
		if(list != null && list.size() > 0) {
//			Date initDate = DateUtils.parseDate(INIT_TIME, DATE_FORMAT);
//			List<String> listAll = posInfoRepository.countMonitorPosData(initDate, nowDate);
			// 发送邮件
			ResultVo sendEmailResult = null;
			try {
				String email = paramService.findMonitorEmail("参卡号监控邮箱");
				String content = getContent(list);
				Map<String, Object> smsInfo = Maps.newHashMap();
				smsInfo.put("to", email); // 收件人邮箱地址 zhuyishan@shanlinjinrong.com
				smsInfo.put("type", MailType.TEXT);
				smsInfo.put("title", "监控脚本-监控一个POS单线上线下同时使用");
				smsInfo.put("content", String.format("警告：当前系统存在 %s 笔相同的参考号。\r%s", list.size(), content));
				smsInfo.put("ftlPath", Constant.BIND_MAIL_TEMPLATE);
				
				log.info("准备发送邮件给", smsInfo.get("to"));
				sendEmailResult = emailService.sendEmail(smsInfo);
				log.info("发送邮件处理结果：", sendEmailResult.getValue("message"));
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			if (!ResultVo.isSuccess(sendEmailResult)) {
				log.error("发送邮件失败");
				return; // 发送失败不更新
			}
		}
//		// 设置Redis的Key为WAITING_AUDIT_DAT的值为now
//		monitorDate = DateUtils.formatDate(nowDate, DATE_FORMAT);
//		redisTemplate2.opsForValue().set(Constant.MONITOR_DATE, monitorDate);
	}

	private String getContent(List<String> list) {
		StringBuilder str = new StringBuilder();
		str.append("[");
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				str.append(list.get(i));
			} else {
				str.append(",").append(list.get(i));
			}
		}
		str.append("]");
		return str.toString();
	}

}
