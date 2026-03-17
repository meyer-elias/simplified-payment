package com.eliasmeyer.sp.infrastructure.config;

import com.eliasmeyer.sp.core.application.ports.out.EventStorePort;
import com.eliasmeyer.sp.core.application.shared.OutboxDomainEventDispatcher;
import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class DomainEventDispatcherConfig {

	@Produces
	@Dependent
	public DomainEventDispatcher domainEventDispatcher(AppLogger appLogger,
		EventStorePort eventStorePort) {
		return new OutboxDomainEventDispatcher(appLogger, eventStorePort);
	}

}
