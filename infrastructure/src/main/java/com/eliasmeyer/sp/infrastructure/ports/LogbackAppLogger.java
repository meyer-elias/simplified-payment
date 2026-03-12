package com.eliasmeyer.sp.infrastructure.ports;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LogbackAppLogger implements AppLogger {

	private final Logger logger;

	public LogbackAppLogger() {
		this.logger = LoggerFactory.getLogger(LogbackAppLogger.class);
	}

	public LogbackAppLogger(Class<?> clazz) {
		this.logger = LoggerFactory.getLogger(clazz);
	}

	@Override
	public void debug(String message, Object... args) {
		if (logger.isDebugEnabled()) {
			logger.debug(message, args);
		}
	}

	@Override
	public void info(String message, Object... args) {
		if (logger.isInfoEnabled()) {
			logger.info(message, args);
		}
	}

	@Override
	public void warn(String message, Object... args) {
		if (logger.isWarnEnabled()) {
			logger.warn(message, args);
		}
	}

	@Override
	public void error(String message, Throwable throwable, Object... args) {
		if (logger.isErrorEnabled()) {
			logger.error(message, args, throwable);
		}
	}
}
