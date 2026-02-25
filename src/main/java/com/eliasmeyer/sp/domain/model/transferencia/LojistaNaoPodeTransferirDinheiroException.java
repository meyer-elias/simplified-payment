package com.eliasmeyer.sp.domain.model.transferencia;

import com.eliasmeyer.sp.domain.shared.DomainException;

public class LojistaNaoPodeTransferirDinheiroException extends DomainException {

    public LojistaNaoPodeTransferirDinheiroException(String message) {
        super(message);
    }
}
