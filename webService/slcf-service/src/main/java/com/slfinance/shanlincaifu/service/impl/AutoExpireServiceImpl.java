package com.slfinance.shanlincaifu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.service.AutoExpireService;
import com.slfinance.vo.ResultVo;

@Service("autoExpireService")
public class AutoExpireServiceImpl implements AutoExpireService{

	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
	
	/**
	 * 更新过期红包状态
	 * @return
	 * @throws SLException
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo AutoExpireDay() throws SLException {
		
		List<CustActivityInfoEntity> list = custActivityInfoRepository.findBySystemDate();
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				CustActivityInfoEntity custActivityInfo= list.get(i);
				custActivityInfo.setTradeStatus("已过期");
			}
		}else {
			return new ResultVo(false, "没有过期的红包");
		}
		return new ResultVo(true,"更新成功");
	}

}
