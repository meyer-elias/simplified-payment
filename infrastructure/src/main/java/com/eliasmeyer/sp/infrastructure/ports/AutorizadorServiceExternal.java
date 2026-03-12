package com.eliasmeyer.sp.infrastructure.ports;

import com.eliasmeyer.sp.core.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.core.domain.shared.identifier.Id;
import com.eliasmeyer.sp.infrastructure.ports.client.AutorizacaoTransferenciaResponse;
import com.eliasmeyer.sp.infrastructure.ports.client.AutorizadorTransferenciaClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AutorizadorServiceExternal implements TransferenciaAutorizadorOutputPort {

	private final AppLogger appLogger;

	private final AutorizadorTransferenciaClient autorizador;

	@Inject
	public AutorizadorServiceExternal(AppLogger appLogger,
		@RestClient AutorizadorTransferenciaClient autorizador) {
		this.appLogger = appLogger;
		this.autorizador = autorizador;
	}

	@Override
	public boolean isAutorizado(Id idUsuario) {
		try {
			AutorizacaoTransferenciaResponse response = autorizador.verificar(idUsuario.asString());
			return response.authorized();
		} catch (Exception e) {
			appLogger.error("Error ao consultar autorizador externo.", e);
			return false;
		}
	}
}
