package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
class OutboxEventJpaAdapter implements PanacheRepositoryBase<OutboxEventEntity, UUID> {

	private static final int BATCH_SIZE = 50; // evita processar tudo de uma vez

	public List<OutboxEventEntity> findPendingEvents() {
		return find("status = ?1 ORDER BY occurredOn ASC", OutboxEventStatus.PENDENTE)
			.page(0, BATCH_SIZE)
			.list();
	}

	public void marcarComoPublicado(UUID id) {
		update("status = ?1, publishedAt = ?2 WHERE id = ?3",
			OutboxEventStatus.PUBLICADO, Instant.now(), id);
	}

	public void marcarComoFalhado(UUID id) {
		update("status = ?1 WHERE id = ?2",
			OutboxEventStatus.FALHADO, id);
	}
}
