package com.eliasmeyer.sp.infrastructure.ports;

import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.application.shared.ApplicationException;
import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.TransactionManager;

@ApplicationScoped
public class QuarkusTransactionManager implements AppTransactionManager {

	private final TransactionManager transactionManager;

	private final AppLogger appLogger;

	public QuarkusTransactionManager(TransactionManager transactionManager,
		AppLogger appLogger) {
		this.transactionManager = transactionManager;
		this.appLogger = appLogger;
	}

	@Override
	public void execute(Runnable action) {
		try {
			transactionManager.begin();
			action.run();
			transactionManager.commit();
		} catch (Exception e) {
			appLogger.error("Erro ao efetivar transação: ", e);
			try {
				transactionManager.rollback();
			} catch (Exception rollback) {
				appLogger.error("Erro ao efetuar rollback da transação.", rollback);
			}
			throw new ApplicationException("Transaction failed", e);
		}
	}
}
