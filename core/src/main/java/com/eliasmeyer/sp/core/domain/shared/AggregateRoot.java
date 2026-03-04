package com.eliasmeyer.sp.core.domain.shared;

import com.eliasmeyer.sp.core.domain.shared.identifier.Identifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class AggregateRoot<I extends Identifier<?>> extends Entity<I> {

	private final List<DomainEvent> domainEvents = new ArrayList<>();

	protected AggregateRoot(I id) {
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
