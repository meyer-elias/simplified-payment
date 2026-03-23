package com.eliasmeyer.sp.infrastructure.messaging;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.infrastructure.persistence.eventstore.OutboxEventStoreRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class OutboxScheduler {

	private final OutboxEventStoreRepository outboxRepository;

	private final Emitter<String> emitter;
	private final AppLogger appLogger;

	@Inject
	public OutboxScheduler(OutboxEventStoreRepository outboxRepository,
		@Channel(CanaisMensagensConstants.NOTIFICACAO_TRANSFERENCIA) Emitter<String> emitter,
		AppLogger appLogger) {
		this.outboxRepository = outboxRepository;
		this.emitter = emitter;
		this.appLogger = appLogger;
	}

	@Scheduled(every = "5s")
	void publicar() {
		outboxRepository.buscarPendentes().forEach(event -> {
			try {
				emitter.send(event.getPayload());
				outboxRepository.marcarComoPublicado(event.getId());
				appLogger.info("Evento [{}] publicado com sucesso", event.getId());
			} catch (Exception e) {
				appLogger.error("Falha ao publicar evento [{}]", e, event.getId());
				outboxRepository.marcarComoFalhado(event.getId());
			}
		});
	}
}
