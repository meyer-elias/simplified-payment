package com.eliasmeyer.sp.core.domain.exception;

import com.eliasmeyer.sp.core.domain.shared.DomainException;

public class SaldoInsuficienteException extends DomainException {

	public SaldoInsuficienteException(String message) {
		super(message);
	}

}
