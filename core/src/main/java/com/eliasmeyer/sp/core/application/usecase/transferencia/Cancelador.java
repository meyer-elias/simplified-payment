package com.eliasmeyer.sp.core.application.usecase.transferencia;


import com.eliasmeyer.sp.core.application.ports.TransactionManager;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.usuario.UsuarioOutputPort;

class Cancelador {

	private final TransactionManager transactionManager;

	private final UsuarioOutputPort usuarioOutputPort;

	private final TransferenciaOutputPort transferenciaOutputPort;

	public Cancelador(TransactionManager transactionManager, UsuarioOutputPort usuarioOutputPort,
		TransferenciaOutputPort transferenciaOutputPort) {
		this.transactionManager = transactionManager;
		this.usuarioOutputPort = usuarioOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
	}

	public void execute(Transferencia transferencia) {
		transactionManager.execute(() -> cancelar(transferencia));
	}

	void cancelar(Transferencia transferencia) {
		transferencia.cancelar();
		transferenciaOutputPort.salvar(transferencia);
		usuarioOutputPort.salvar(transferencia.getPagador());
	}
}

