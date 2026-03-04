package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.application.ports.TransactionManager;
import com.eliasmeyer.sp.core.application.shared.ApplicationException;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.usuario.UsuarioOutputPort;

class Falhador {

	private final TransactionManager transactionManager;

	private final UsuarioOutputPort usuarioOutputPort;

	private final TransferenciaOutputPort transferenciaOutputPort;

	Falhador(TransactionManager transactionManager, UsuarioOutputPort usuarioOutputPort,
		TransferenciaOutputPort transferenciaOutputPort) {
		this.transactionManager = transactionManager;
		this.usuarioOutputPort = usuarioOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
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
			throw new ApplicationException(
				String.format(
					"Não foi possível marcar transferência %s como falhada — estado atual incompatível",
					transferencia.getId()),
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
			// BD fora: reversão em memória já foi feita
			throw new ApplicationException(
				String.format(
					"Falha ao persistir estado de transferência %s falhada — reversão em memória garantida.",
					transferencia.getId()),
				saveEx);
		}
	}
}



