package com.eliasmeyer.sp.core.application.usecase.transferencia;


import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;

class Cancelador {

	private final AppTransactionManager appTransactionManager;

	private final CarteiraOutputPort carteiraOutputPort;

	private final TransferenciaOutputPort transferenciaOutputPort;

	public Cancelador(AppTransactionManager appTransactionManager,
		CarteiraOutputPort carteiraOutputPort,
		TransferenciaOutputPort transferenciaOutputPort) {
		this.appTransactionManager = appTransactionManager;
		this.carteiraOutputPort = carteiraOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
	}

	public void execute(Transferencia transferencia) {
		appTransactionManager.execute(() -> cancelar(transferencia));
	}

	void cancelar(Transferencia transferencia) {
		transferencia.cancelar();
		transferenciaOutputPort.salvar(transferencia);
		carteiraOutputPort.salvar(transferencia.getPagador());
	}
}

