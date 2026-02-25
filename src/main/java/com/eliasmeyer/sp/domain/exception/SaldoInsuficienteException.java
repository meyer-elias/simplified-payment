package com.eliasmeyer.sp.domain.exception;

import com.eliasmeyer.sp.domain.shared.DomainException;

public class SaldoInsuficienteException extends DomainException {

    SaldoInsuficienteException(String message) {
        super(message);
    }

}
