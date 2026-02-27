package com.eliasmeyer.sp.application.exception;

import com.eliasmeyer.sp.application.shared.ApplicationException;

public class TransferenciaNaoAutorizadaException extends ApplicationException {

	public TransferenciaNaoAutorizadaException(String message) {
		super(message);
	}
}

