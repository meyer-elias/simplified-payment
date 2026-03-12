package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;

class Efetivador {

	private final TransferenciaOutputPort transferenciaOutputPort;

	private final CarteiraOutputPort carteiraOutputPort;

	private final AppTransactionManager appTransactionManager;

	Efetivador(TransferenciaOutputPort transferenciaOutputPort,
		CarteiraOutputPort carteiraOutputPort,
		AppTransactionManager appTransactionManager) {
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.carteiraOutputPort = carteiraOutputPort;
		this.appTransactionManager = appTransactionManager;
	}

	void executar(Transferencia transferencia) {
		appTransactionManager.execute(() -> efetivar(transferencia));
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

