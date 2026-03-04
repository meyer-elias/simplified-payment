package com.eliasmeyer.sp.core.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaFalhadaEvento;
import com.eliasmeyer.sp.core.domain.shared.DomainEventHandler;

public class TransferenciaFalhadaHandler implements
	DomainEventHandler<TransferenciaFalhadaEvento> {

	private final AppLogger appLogger;

	public TransferenciaFalhadaHandler(AppLogger appLogger) {
		this.appLogger = appLogger;
	}

	@Override
	public Class<TransferenciaFalhadaEvento> eventType() {
		return TransferenciaFalhadaEvento.class;
	}

	@Override
	public void handle(TransferenciaFalhadaEvento event) {
		appLogger.debug("### Início transferencia de quantia | FALHADA ###");
		appLogger.debug("### Transferencia: {%s} ", event.transferencia());
		appLogger.debug("### Fim transferência de quantia | FALHADA ###");
	}
}
