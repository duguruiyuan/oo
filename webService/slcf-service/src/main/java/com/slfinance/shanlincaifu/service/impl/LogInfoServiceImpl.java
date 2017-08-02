package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.service.LogInfoService;

@Service("logInfoService")
public class LogInfoServiceImpl implements LogInfoService {

	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	@Autowired
	private CustInfoRepository custInfoRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	public Map<String, Object> findOperationLogByCustId(Map<String, Object> param) {
		String logType = (String) param.get("logType");
		String operPerson = (String) param.get("operPerson");
		int start = (Integer) param.get("start");
		int length = (Integer)param.get("length");
		int currentPage = start /length;
		PageRequest pageable = new PageRequest(currentPage, (Integer) param.get("length"));
		Map<String, Object> result = new HashMap<String, Object>();
		Page<LogInfoEntity> page = null;
		if (StringUtils.isEmpty(logType)) {
			// page =
			// logInfoEntityRepository.findLogInfoEntityByRelatePrimaryAndRelateType(relatePrimary,
			// relateType, pageable);

			page = logInfoEntityRepository.findLogInfoEntityByOperPerson(operPerson, pageable);
		} else {
			page = logInfoEntityRepository.findLogInfoEntityByLogType(logType, operPerson, pageable);
		}
		result.put("iTotalDisplayRecords", page.getTotalElements());
		for (LogInfoEntity lie : page.getContent()) {
			if (!StringUtils.isEmpty(lie.getCreateUser())) {
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(lie.getCreateUser());
				if (StringUtils.isEmpty(custInfoEntity)) {
					String userName = userRepository.findUserNameById(lie.getCreateUser());
					if (!StringUtils.isEmpty(userName) && !"".equals(userName))
						lie.setOperPerson(userName);
					else
						lie.setOperPerson(lie.getCreateUser());
				} else {
					lie.setOperPerson(custInfoEntity.getLoginName());
				}

			}
		}
		result.put("data", page.getContent());
		return result;
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public LogInfoEntity saveLogInfo(LogInfoEntity logInfo) {
		logInfo = logInfoEntityRepository.save(logInfo);
		return logInfo;
	}

}
