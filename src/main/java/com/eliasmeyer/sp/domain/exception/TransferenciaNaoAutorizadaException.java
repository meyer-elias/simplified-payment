package com.eliasmeyer.sp.domain.exception;

import com.eliasmeyer.sp.domain.shared.DomainException;

public class TransferenciaNaoAutorizadaException extends DomainException {

  public TransferenciaNaoAutorizadaException(String message) {
    super(message);
  }
}
