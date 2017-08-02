package com.slfinance.shanlincaifu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 参数校验异常
 * @author 强
 *
 */
@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class IllegalPerameterException extends SLBException{

	public IllegalPerameterException(String messages) {
		super(messages);
	}

}
