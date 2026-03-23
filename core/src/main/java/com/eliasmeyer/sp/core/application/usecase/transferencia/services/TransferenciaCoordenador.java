package com.eliasmeyer.sp.core.application.usecase.transferencia.services;

import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;

public class TransferenciaCoordenador {

	private final Reservador reservador;

	private final Cancelador cancelador;

	private final Efetivador efetivador;

	private final Falhador falhador;

	public TransferenciaCoordenador(
		AppTransactionManager appTransactionManager,
		CarteiraOutputPort carteiraOutputPort,
		TransferenciaOutputPort transferenciaOutputPort) {
		this.reservador = new Reservador(transferenciaOutputPort, carteiraOutputPort,
			appTransactionManager);
		this.cancelador = new Cancelador(appTransactionManager, carteiraOutputPort,
			transferenciaOutputPort);
		this.efetivador = new Efetivador(transferenciaOutputPort, carteiraOutputPort,
			appTransactionManager);
		this.falhador = new Falhador(appTransactionManager, carteiraOutputPort,
			transferenciaOutputPort);
	}

	public void reservar(Transferencia transferencia) {
		reservador.executar(transferencia);
	}

	public void cancelar(Transferencia transferencia) {
		cancelador.execute(transferencia);
	}

	public void efetivar(Transferencia transferencia) {
		efetivador.executar(transferencia);
	}

	public void falhar(Transferencia transferencia) {
		falhador.execute(transferencia);
	}
}
