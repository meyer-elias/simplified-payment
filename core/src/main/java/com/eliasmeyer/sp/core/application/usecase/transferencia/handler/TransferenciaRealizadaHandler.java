package com.eliasmeyer.sp.core.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaRealizadaEvento;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaNotificadorOutputPort;
import com.eliasmeyer.sp.core.domain.shared.DomainEventHandler;

public class TransferenciaRealizadaHandler implements
	DomainEventHandler<TransferenciaRealizadaEvento> {

	private final TransferenciaNotificadorOutputPort transferenciaNotificadorOutputPort;

	public TransferenciaRealizadaHandler(
		TransferenciaNotificadorOutputPort transferenciaNotificadorOutputPort) {
		this.transferenciaNotificadorOutputPort = transferenciaNotificadorOutputPort;
	}

	@Override
	public Class<TransferenciaRealizadaEvento> eventType() {
		return TransferenciaRealizadaEvento.class;
	}

	@Override
	public void handle(TransferenciaRealizadaEvento event) {
		transferenciaNotificadorOutputPort.notificar(event.transferencia());
	}
}
