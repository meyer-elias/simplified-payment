package com.eliasmeyer.sp.domain.model.carteira;

import com.eliasmeyer.sp.domain.commons.exception.BusinessRuleException;

public class SaldoInsuficienteException extends BusinessRuleException {

    SaldoInsuficienteException(String message) {
        super(message);
    }

}
