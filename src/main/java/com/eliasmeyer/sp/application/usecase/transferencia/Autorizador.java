package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;

class Autorizador {

	private final TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort;

	private final AppLogger appLogger;

	Autorizador(TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort,
		AppLogger appLogger) {

		this.transferenciaAutorizadorOutputPort = transferenciaAutorizadorOutputPort;
		this.appLogger = appLogger;
	}

	boolean isAutorizado(Transferencia transferencia) {
		UsuarioId idPagador = transferencia.getPagador().getId();
		try {
			return transferenciaAutorizadorOutputPort.isAutorizado(idPagador);
		} catch (Exception e) {
			appLogger.error("Erro no Serviço do autorizador! Pagador: {}, Transferência: {}",
				e, idPagador, transferencia.getId());
			throw new AutorizadorIndisponivelException("Erro no Serviço do autorizador", e);
		}
	}
}

