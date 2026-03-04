package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;

class Autorizador {

	private final TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort;

	Autorizador(TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort) {
		this.transferenciaAutorizadorOutputPort = transferenciaAutorizadorOutputPort;
	}

	boolean isAutorizado(Transferencia transferencia) {
		UsuarioId idPagador = transferencia.getPagador().getId();
		try {
			return transferenciaAutorizadorOutputPort.isAutorizado(idPagador);
		} catch (Exception e) {
			throw new AutorizadorIndisponivelException(
				String.format("Erro no Serviço do autorizador! Pagador: %s, Transferência: %s",
					idPagador, transferencia.getId()), e);
		}
	}
}

