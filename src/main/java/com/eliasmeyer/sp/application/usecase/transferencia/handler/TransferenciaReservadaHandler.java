package com.eliasmeyer.sp.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaReservadaEvento;
import com.eliasmeyer.sp.domain.shared.DomainEventHandler;

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
