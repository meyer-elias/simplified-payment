package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.application.ports.TransactionManager;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;

class Efetivador {

	private final TransferenciaOutputPort transferenciaOutputPort;

	private final CarteiraOutputPort carteiraOutputPort;

	private final TransactionManager transactionManager;

	Efetivador(TransferenciaOutputPort transferenciaOutputPort,
		CarteiraOutputPort carteiraOutputPort,
		TransactionManager transactionManager) {
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.carteiraOutputPort = carteiraOutputPort;
		this.transactionManager = transactionManager;
	}

	void executar(Transferencia transferencia) {
		transactionManager.execute(() -> efetivar(transferencia));
	}

	private void efetivar(Transferencia transferencia) {
		Carteira cPagador = transferencia.getPagador();
		Carteira cRecebedor = transferencia.getRecebedor();

		transferencia.realizar();
		transferenciaOutputPort.salvar(transferencia);
		carteiraOutputPort.salvar(cPagador);
		carteiraOutputPort.salvar(cRecebedor);
	}
}

