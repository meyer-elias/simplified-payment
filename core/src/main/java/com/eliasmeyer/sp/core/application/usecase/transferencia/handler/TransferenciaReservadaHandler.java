package com.eliasmeyer.sp.core.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaReservadaEvento;
import com.eliasmeyer.sp.core.domain.shared.DomainEventHandler;

public class TransferenciaReservadaHandler implements
	DomainEventHandler<TransferenciaReservadaEvento> {

	private final AppLogger appLogger;

	public TransferenciaReservadaHandler(AppLogger appLogger) {
		this.appLogger = appLogger;
	}

	@Override
	public Class<TransferenciaReservadaEvento> eventType() {
		return TransferenciaReservadaEvento.class;
	}

	@Override
	public void handle(TransferenciaReservadaEvento event) {
		appLogger.debug("### Início transferencia de quantia | RESERVADA ###");
		appLogger.debug("### Transferencia: {%s} ", event.transferencia());
		appLogger.debug("### Fim transferência de quantia | RESERVADA ###");
	}
}
