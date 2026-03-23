package com.eliasmeyer.sp.core.application.usecase.transferencia.services;

import com.eliasmeyer.sp.core.application.exception.TransferenciaRejeitadaException;
import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.application.shared.ApplicationException;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.exception.CarteiraException;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.model.transferencia.exception.TransferenciaException;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;

class Reservador {

	private final TransferenciaOutputPort transferenciaOutputPort;

	private final CarteiraOutputPort carteiraOutputPort;

	private final AppTransactionManager appTransactionManager;

	Reservador(TransferenciaOutputPort transferenciaOutputPort,
		CarteiraOutputPort carteiraOutputPort, AppTransactionManager appTransactionManager) {
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.carteiraOutputPort = carteiraOutputPort;
		this.appTransactionManager = appTransactionManager;
	}

	void executar(Transferencia transferencia) {
		appTransactionManager.execute(() -> reservar(transferencia));
	}

	private void reservar(Transferencia transferencia) {
		Carteira cPagador = transferencia.getPagador();
		try {
			transferencia.reservar();
			transferenciaOutputPort.salvar(transferencia);
			carteiraOutputPort.salvar(cPagador);
		} catch (CarteiraException | TransferenciaException e) {
			throw new TransferenciaRejeitadaException(
				"Transferência não realizada por violação da regra de negócio.", e);
		} catch (Exception e) {
			throw new ApplicationException(
				String.format("Erro técnico ao reservar quantia financeira para transferência %s",
					transferencia.getId()), e);
		}
	}
}

