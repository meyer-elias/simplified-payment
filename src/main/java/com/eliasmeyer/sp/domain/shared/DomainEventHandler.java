package com.eliasmeyer.sp.domain.shared;

public interface DomainEventHandler<T extends DomainEvent> {

    void handle(T event);

    Class<T> eventType();
}
