package com.eliasmeyer.sp.domain.exception;

import com.eliasmeyer.sp.domain.shared.DomainException;

public class LojistaNaoPodeTransferirDinheiroException extends DomainException {

    public LojistaNaoPodeTransferirDinheiroException(String message) {
        super(message);
    }
}
