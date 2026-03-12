package com.eliasmeyer.sp.infrastructure.config;

import com.eliasmeyer.sp.core.application.shared.DefaultDomainEventDispatcher;
import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.util.ArrayList;

@ApplicationScoped
public class DomainEventDispatcherConfig {

	@Produces
	public DomainEventDispatcher domainEventDispatcher(AppLogger appLogger) {
		return new DefaultDomainEventDispatcher(appLogger, new ArrayList<>());
	}

}
