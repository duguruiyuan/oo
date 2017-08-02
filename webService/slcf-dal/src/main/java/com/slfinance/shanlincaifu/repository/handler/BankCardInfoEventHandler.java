package com.slfinance.shanlincaifu.repository.handler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;

@Slf4j
@RepositoryEventHandler(BankCardInfoEntity.class)
public class BankCardInfoEventHandler {

		@Autowired
		BankCardInfoRepository bankCardInfoRepository;
	 @HandleBeforeSave
	  public void handleBankCardInfoSave(BankCardInfoEntity p) {
		 if(p.getIsDefault().equals("1")) {
			 String custId = p.getCustInfoEntity().getId();
			 BankCardInfoEntity currentDefault =  bankCardInfoRepository.findCustDefaultBank(custId);
			 if(p.getId() != currentDefault.getId()) {
				 currentDefault.setIsDefault("0");
				 bankCardInfoRepository.save(currentDefault);
			 }
		 }
		 log.debug("before save bankCard");
	  }

}
