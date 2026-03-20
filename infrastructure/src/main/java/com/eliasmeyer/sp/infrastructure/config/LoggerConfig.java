package com.eliasmeyer.sp.infrastructure.config;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.infrastructure.ports.JBossAppLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LoggerConfig {

	@Produces
	@Dependent
	public AppLogger createLogger(InjectionPoint injectionPoint) {
		Class<?> clazz = injectionPoint.getMember().getDeclaringClass();
		return new JBossAppLogger(clazz);
	}
}
