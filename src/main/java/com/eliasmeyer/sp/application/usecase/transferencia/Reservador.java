package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.exception.TransferenciaIllegalException;
import com.eliasmeyer.sp.application.ports.TransactionManager;
import com.eliasmeyer.sp.application.shared.ApplicationException;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;
import com.eliasmeyer.sp.domain.shared.DomainException;

class Reservador {

	private final TransferenciaOutputPort transferenciaOutputPort;
	private final UsuarioOutputPort usuarioOutputPort;
	private final TransactionManager transactionManager;

	Reservador(TransferenciaOutputPort transferenciaOutputPort,
		UsuarioOutputPort usuarioOutputPort, TransactionManager transactionManager) {
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.usuarioOutputPort = usuarioOutputPort;
		this.transactionManager = transactionManager;
	}

	void executar(Transferencia transferencia) {
		transactionManager.execute(() -> reservar(transferencia));
	}

	private void reservar(Transferencia transferencia) {
		Usuario uPagador = transferencia.getPagador();
		try {
			transferencia.reservar();
			transferenciaOutputPort.salvar(transferencia);
			usuarioOutputPort.salvar(uPagador);
		} catch (DomainException e) {
			throw new TransferenciaIllegalException(
				"Transferência inválida por restrição da regra de negócio.", e);
		} catch (Exception e) {
			throw new ApplicationException(
				String.format("Erro técnico ao reservar quantia financeira para transferência %s",
					transferencia.getId()), e);
		}
	}
}

