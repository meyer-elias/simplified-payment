package com.eliasmeyer.sp.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaCanceladaEvento;
import com.eliasmeyer.sp.domain.shared.DomainEventHandler;

public class TransferenciaCanceladaHandler implements
	DomainEventHandler<TransferenciaCanceladaEvento> {

	private final AppLogger appLogger;

	public TransferenciaCanceladaHandler(AppLogger appLogger) {
		this.appLogger = appLogger;
	}

	@Override
	public Class<TransferenciaCanceladaEvento> eventType() {
		return TransferenciaCanceladaEvento.class;
	}

	@Override
	public void handle(TransferenciaCanceladaEvento event) {
		appLogger.debug("### Início transferencia de quantia | CANCELADA ###");
		appLogger.debug("### Transferencia: {%s} ", event.transferencia());
		appLogger.debug("### Fim transferência de quantia | CANCELADA ###");
	}
}
