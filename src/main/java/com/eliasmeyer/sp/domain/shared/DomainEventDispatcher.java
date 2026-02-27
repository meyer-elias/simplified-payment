package com.eliasmeyer.sp.domain.shared;

import java.util.List;

public interface DomainEventDispatcher {

	void dispatch(List<DomainEvent> events);

}
