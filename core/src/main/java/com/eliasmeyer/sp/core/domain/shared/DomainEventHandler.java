package com.eliasmeyer.sp.core.domain.shared;

public interface DomainEventHandler<T extends DomainEvent> {

	void handle(T event);

	Class<T> eventType();
}
