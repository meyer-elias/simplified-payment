package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.application.ports.TransactionManager;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.usuario.UsuarioOutputPort;

class Efetivador {

	private final TransferenciaOutputPort transferenciaOutputPort;
	private final UsuarioOutputPort usuarioOutputPort;
	private final TransactionManager transactionManager;

	Efetivador(TransferenciaOutputPort transferenciaOutputPort,
		UsuarioOutputPort usuarioOutputPort,
		TransactionManager transactionManager) {
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.usuarioOutputPort = usuarioOutputPort;
		this.transactionManager = transactionManager;
	}

	void executar(Transferencia transferencia) {
		transactionManager.execute(() -> efetivar(transferencia));
	}

	private void efetivar(Transferencia transferencia) {
		Usuario uPagador = transferencia.getPagador();
		Usuario uRecebedor = transferencia.getRecebedor();

		transferencia.realizar();
		transferenciaOutputPort.salvar(transferencia);
		usuarioOutputPort.salvar(uPagador);
		usuarioOutputPort.salvar(uRecebedor);
	}
}

