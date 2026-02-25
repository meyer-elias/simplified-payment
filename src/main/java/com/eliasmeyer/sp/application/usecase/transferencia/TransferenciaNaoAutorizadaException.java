package com.eliasmeyer.sp.application.usecase.transferencia;

public class TransferenciaNaoAutorizadaException extends RuntimeException {
    public TransferenciaNaoAutorizadaException(String message) {
        super(message);
    }
}
