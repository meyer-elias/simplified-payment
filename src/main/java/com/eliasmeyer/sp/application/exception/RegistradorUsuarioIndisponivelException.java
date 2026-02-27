package com.eliasmeyer.sp.application.exception;

import com.eliasmeyer.sp.application.shared.ApplicationException;

public class RegistradorUsuarioIndisponivelException extends ApplicationException {

	public RegistradorUsuarioIndisponivelException(String message) {
		super(message);
	}

	public RegistradorUsuarioIndisponivelException(String message, Throwable cause) {
		super(message, cause);
	}
}
