package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
class OutboxEventJpaAdapter implements PanacheRepositoryBase<OutboxEventEntity, UUID> {

}
