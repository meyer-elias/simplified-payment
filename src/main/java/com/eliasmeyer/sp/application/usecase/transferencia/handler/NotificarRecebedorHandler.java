package com.eliasmeyer.sp.application.usecase.transferencia.handler;

import com.eliasmeyer.sp.application.port.transferencia.out.NotificacaoOutputPort;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaRealizadaEvento;
import com.eliasmeyer.sp.domain.shared.DomainEventHandler;

public class NotificarRecebedorHandler implements DomainEventHandler<TransferenciaRealizadaEvento> {

    private final NotificacaoOutputPort notificacaoOutputPort;

    public NotificarRecebedorHandler(NotificacaoOutputPort notificacaoOutputPort) {
        this.notificacaoOutputPort = notificacaoOutputPort;
    }

    @Override
    public Class<TransferenciaRealizadaEvento> eventType() {
        return TransferenciaRealizadaEvento.class;
    }

    @Override
    public void handle(TransferenciaRealizadaEvento event) {
        notificacaoOutputPort.notificar(event.getId());
    }
}
