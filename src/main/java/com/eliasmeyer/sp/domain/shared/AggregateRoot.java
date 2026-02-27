package com.eliasmeyer.sp.domain.shared;

import com.eliasmeyer.sp.domain.shared.identifier.Identifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class AggregateRoot<ID extends Identifier<?>> extends Entity<ID> {

	private final List<DomainEvent> domainEvents = new ArrayList<>();

	protected AggregateRoot(ID id) {
		super(id);
	}

	protected void registerEvent(Supplier<DomainEvent> eventSupplier) {
		domainEvents.add(eventSupplier.get());
	}

	public List<DomainEvent> domainEvents() {
		return Collections.unmodifiableList(domainEvents);
	}

	public void clearEvents() {
		domainEvents.clear();
	}
}
