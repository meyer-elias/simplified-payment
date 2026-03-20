package com.eliasmeyer.sp.infrastructure.ports;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import org.jboss.logging.Logger;

public class JBossAppLogger implements AppLogger {

	private final Logger logger;

	public JBossAppLogger(Class<?> clazz) {
		this.logger = Logger.getLogger(clazz);
	}

	@Override
	public void debug(String message, Object... args) {
		logger.debugf(message, args);
	}

	@Override
	public void info(String message, Object... args) {
		logger.infof(message, args);
	}

	@Override
	public void warn(String message, Object... args) {
		logger.warnf(message, args);
	}

	@Override
	public void error(String message, Throwable throwable, Object... args) {
		logger.errorf(throwable, message, args);
	}
}
