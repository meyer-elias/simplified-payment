package com.eliasmeyer.sp.core.application.exception;

import com.eliasmeyer.sp.core.application.shared.ApplicationException;

public class TransferenciaIndisponivelException extends ApplicationException {

	public TransferenciaIndisponivelException(String message) {
		super(message);
	}

	public TransferenciaIndisponivelException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
