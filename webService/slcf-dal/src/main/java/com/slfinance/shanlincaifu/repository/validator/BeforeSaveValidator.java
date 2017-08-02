package com.slfinance.shanlincaifu.repository.validator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BeforeSaveValidator implements Validator{

	@Autowired
	Map<String, SaveValidator> validators;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return validatorExist(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Validator v = getValidator(target);
		v.validate(target, errors);
	}

	private boolean validatorExist(Class<?> clazz) {
		String key = "beforeSave" + clazz.getSimpleName() + "Validator";
		return validators.containsKey(key);
	}
	
	private Validator getValidator(Object target) {
		String key = "beforeSave" + target.getClass().getSimpleName() + "Validator";
		return validators.get(key);
	}
}
