package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.UUID;

@Repository
public interface OutboxEventJpaAdapter extends CrudRepository<OutboxEventEntity, UUID> {

}
