package com.eliasmeyer.sp.core.application.exception;


import com.eliasmeyer.sp.core.application.shared.ApplicationException;

public class TransferenciaNaoAutorizadaException extends ApplicationException {

	public TransferenciaNaoAutorizadaException(String message) {
		super(message);
	}
}

