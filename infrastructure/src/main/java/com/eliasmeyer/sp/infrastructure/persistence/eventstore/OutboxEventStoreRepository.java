package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

import com.eliasmeyer.sp.core.application.ports.out.EventStorePort;
import com.eliasmeyer.sp.core.domain.shared.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OutboxEventStoreRepository implements EventStorePort {

	private final ObjectMapper objectMapper;

	private final OutboxEventJpaAdapter outboxEventJpaAdapter;

	@Inject
	public OutboxEventStoreRepository(ObjectMapper objectMapper,
		OutboxEventJpaAdapter outboxEventJpaAdapter) {
		this.objectMapper = objectMapper;
		this.outboxEventJpaAdapter = outboxEventJpaAdapter;
	}

	@Override
	public void store(List<DomainEvent> events) {
		List<OutboxEventEntity> entities = events.stream()
			.map(this::toEntity)
			.toList();

		outboxEventJpaAdapter.persist(entities);
	}

	public List<OutboxEventEntity> buscarPendentes() {
		return outboxEventJpaAdapter.findPendingEvents();
	}

	@Transactional
	public void marcarComoPublicado(UUID id) {
		outboxEventJpaAdapter.marcarComoPublicado(id);
	}

	@Transactional
	public void marcarComoFalhado(UUID id) {
		outboxEventJpaAdapter.marcarComoFalhado(id);
	}

	private OutboxEventEntity toEntity(DomainEvent event) {
		try {
			OutboxEventEntity entity = new OutboxEventEntity();
			entity.setId(UUID.randomUUID());
			entity.setAggregateType(event.aggregateType());
			entity.setAggregateId(event.aggregateId().toString());
			entity.setEventType(event.getClass().getSimpleName());
			entity.setPayload(objectMapper.writeValueAsString(event));
			entity.setOccurredOn(event.occurredOn());
			entity.setStatus(OutboxEventStatus.PENDENTE);
			return entity;
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(
				"Falha ao serializar evento: " + event.getClass().getSimpleName(), e
			);
		}
	}
}
