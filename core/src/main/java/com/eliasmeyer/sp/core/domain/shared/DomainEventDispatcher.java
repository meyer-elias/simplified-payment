package com.eliasmeyer.sp.core.domain.shared;

import java.util.List;

public interface DomainEventDispatcher {

	void dispatch(List<DomainEvent> events);

}
