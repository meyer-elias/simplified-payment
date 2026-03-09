package com.eliasmeyer.sp.core.application.usecase.transferencia;


import com.eliasmeyer.sp.core.application.ports.TransactionManager;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;

class Cancelador {

	private final TransactionManager transactionManager;

	private final CarteiraOutputPort carteiraOutputPort;

	private final TransferenciaOutputPort transferenciaOutputPort;

	public Cancelador(TransactionManager transactionManager, CarteiraOutputPort carteiraOutputPort,
		TransferenciaOutputPort transferenciaOutputPort) {
		this.transactionManager = transactionManager;
		this.carteiraOutputPort = carteiraOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
	}

	public void execute(Transferencia transferencia) {
		transactionManager.execute(() -> cancelar(transferencia));
	}

	void cancelar(Transferencia transferencia) {
		transferencia.cancelar();
		transferenciaOutputPort.salvar(transferencia);
		carteiraOutputPort.salvar(transferencia.getPagador());
	}
}

