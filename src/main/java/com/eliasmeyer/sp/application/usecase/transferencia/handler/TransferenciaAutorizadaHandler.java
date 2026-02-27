package com.eliasmeyer.sp.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaAutorizadaEvento;
import com.eliasmeyer.sp.domain.shared.DomainEventHandler;

public class TransferenciaAutorizadaHandler implements
	DomainEventHandler<TransferenciaAutorizadaEvento> {

	private final AppLogger appLogger;

	public TransferenciaAutorizadaHandler(AppLogger appLogger) {
		this.appLogger = appLogger;
	}

	@Override
	public Class<TransferenciaAutorizadaEvento> eventType() {
		return TransferenciaAutorizadaEvento.class;
	}

	@Override
	public void handle(TransferenciaAutorizadaEvento event) {
		appLogger.debug("### Início transferencia de quantia | AUTORIZADA ###");
		appLogger.debug("### Transferencia: {%s} ", event.transferencia());
		appLogger.debug("### Fim transferência de quantia | AUTORIZADA ###");
	}
}
