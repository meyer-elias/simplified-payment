package com.eliasmeyer.sp.core.application.ports.out;

import com.eliasmeyer.sp.core.domain.shared.DomainEvent;
import java.util.List;

public interface EventPublisherPort {

	void publish(List<DomainEvent> events);

}
