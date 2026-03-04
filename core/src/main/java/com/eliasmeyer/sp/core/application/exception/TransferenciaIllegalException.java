package com.eliasmeyer.sp.core.application.exception;

import com.eliasmeyer.sp.core.application.shared.ApplicationException;

public class TransferenciaIllegalException extends ApplicationException {

	public TransferenciaIllegalException(String message) {
		super(message);
	}

	public TransferenciaIllegalException(String message, Throwable cause) {
		super(message, cause);
	}
}
