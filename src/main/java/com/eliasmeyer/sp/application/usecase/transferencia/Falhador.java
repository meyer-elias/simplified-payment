package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.ports.TransactionManager;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;

class Falhador {

	private final TransactionManager transactionManager;

	private final UsuarioOutputPort usuarioOutputPort;

	private final TransferenciaOutputPort transferenciaOutputPort;

	private final AppLogger appLogger;

	Falhador(TransactionManager transactionManager, UsuarioOutputPort usuarioOutputPort,
		TransferenciaOutputPort transferenciaOutputPort, AppLogger appLogger) {
		this.transactionManager = transactionManager;
		this.usuarioOutputPort = usuarioOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.appLogger = appLogger;
	}

	/**
	 * Reverte a reserva em memória e tenta persistir o estado de falha no BD. Se o BD também
	 * estiver fora, o erro é apenas logado (best-effort): a reversão em memória já é suficiente
	 * para não deixar o saldo preso.
	 * <p>
	 * Se a transferência já não estiver mais em estado reservado (ex: realizar() foi invocado mas o
	 * save falhou), o estado de domínio não é alterado — apenas se tenta persistir.
	 */
	public void execute(Transferencia transferencia) {
		try {
			transferencia.falhar();
		} catch (IllegalStateException ise) {
			appLogger.error(
				"Não foi possível marcar transferência como falhada — estado atual incompatível: {}",
				ise);
		}
		tentarSalvarFalha(transferencia);
	}

	private void tentarSalvarFalha(Transferencia transferencia) {
		try {
			transactionManager.execute(() -> {
				transferenciaOutputPort.salvar(transferencia);
				usuarioOutputPort.salvar(transferencia.getPagador());
			});
		} catch (Exception saveEx) {
			// BD fora: reversão em memória já foi feita; apenas registra para auditoria.
			appLogger.error(
				"Falha ao persistir estado de transferência falhada — reversão em memória garantida.",
				saveEx);
		}
	}
}



