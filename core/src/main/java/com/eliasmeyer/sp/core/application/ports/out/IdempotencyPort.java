package com.eliasmeyer.sp.core.application.ports.out;

public interface IdempotencyPort {

	boolean jaProcessado(String key);

	void registrar(String key);

}
