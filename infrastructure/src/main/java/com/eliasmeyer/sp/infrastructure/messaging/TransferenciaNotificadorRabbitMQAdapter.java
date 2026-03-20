package com.eliasmeyer.sp.infrastructure.messaging;

import com.eliasmeyer.sp.core.application.exception.MensagemNotificacaoException;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaNotificadorOutputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class TransferenciaNotificadorRabbitMQAdapter implements TransferenciaNotificadorOutputPort {

	private final Emitter<String> emitter;

	private final ObjectMapper objectMapper;

	@Inject
	public TransferenciaNotificadorRabbitMQAdapter(
		@Channel("notificacao-transferencia") Emitter<String> emitter,
		ObjectMapper objectMapper) {
		this.emitter = emitter;
		this.objectMapper = objectMapper;
	}

	@Override
	public void notificar(Transferencia transferencia) {
		try {
			var payload = NotificacaoTransferenciaRealizadaPayload.from(transferencia);
			String json = objectMapper.writeValueAsString(payload);
			emitter.send(json);
		} catch (Exception e) {
			throw new MensagemNotificacaoException("Erro ao notificar transferência realizada", e);
		}
	}
}
