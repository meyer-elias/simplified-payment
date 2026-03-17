package com.eliasmeyer.sp.core.application.shared;

import com.eliasmeyer.sp.core.application.ports.out.EventStorePort;
import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.core.domain.shared.DomainEvent;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
import java.util.List;
import java.util.Objects;

/**
 * Implementação do dispatcher de eventos de domínio baseada no Outbox Pattern.
 * <p>
 * Em vez de despachar eventos diretamente para handlers ou brokers, persiste os eventos no
 * EventStore dentro da mesma transação do caso de uso. A publicação efetiva ocorre de forma
 * assíncrona via {@code OutboxScheduler}.
 * </p>
 */
public class OutboxDomainEventDispatcher implements DomainEventDispatcher {

	private final AppLogger appLogger;

	private final EventStorePort eventStorePort;

	public OutboxDomainEventDispatcher(AppLogger appLogger, EventStorePort eventStorePort) {
		this.appLogger = appLogger;
		this.eventStorePort = eventStorePort;
	}

	/**
	 * Persiste os eventos no EventStore (Outbox) dentro da mesma transação do caso de uso.
	 *
	 * @param events lista de eventos a persistir; não pode ser nula
	 * @throws NullPointerException se a lista de eventos for nula
	 */
	@Override
	public void dispatch(List<DomainEvent> events) {
		Objects.requireNonNull(events, "A lista de eventos não pode ser nula");

		if (events.isEmpty()) {
			appLogger.debug("Nenhum evento para despachar");
			return;
		}

		appLogger.debug("Persistindo {} evento(s) no EventStore", events.size());
		eventStorePort.store(events);
		appLogger.info("{} evento(s) persistido(s) no EventStore com sucesso", events.size());
	}
}
