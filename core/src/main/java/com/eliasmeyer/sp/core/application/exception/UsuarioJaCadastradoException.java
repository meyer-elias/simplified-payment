package com.eliasmeyer.sp.core.application.exception;

import com.eliasmeyer.sp.core.domain.shared.DomainException;

public class UsuarioJaCadastradoException extends DomainException {

	public UsuarioJaCadastradoException(String s) {
		super(s);
	}
}
