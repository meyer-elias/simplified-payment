package com.eliasmeyer.sp.infrastructure.config;

import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.application.ports.out.IdempotencyPort;
import com.eliasmeyer.sp.core.application.usecase.transferencia.EfetuarTransferenciaUseCase;
import com.eliasmeyer.sp.core.application.usecase.transferencia.ListarTransferenciaUseCase;
import com.eliasmeyer.sp.core.application.usecase.transferencia.services.TransferenciaCoordenador;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
import com.eliasmeyer.sp.infrastructure.persistence.transferencia.TransferenciaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class UseCaseConfig {

	@Produces
	@ApplicationScoped
	public EfetuarTransferenciaInputPort efetuarTransferenciaUseCase(
		TransferenciaAutorizadorOutputPort autorizador,
		AppTransactionManager appTransactionManager,
		DomainEventDispatcher domainEventDispatcher,
		CarteiraOutputPort carteiraOutputPort,
		TransferenciaOutputPort transferenciaOutputPort,
		IdempotencyPort idempotencyPort) {

		TransferenciaCoordenador coordenador = new TransferenciaCoordenador(
			appTransactionManager, carteiraOutputPort, transferenciaOutputPort);

		return new EfetuarTransferenciaUseCase(
			autorizador,
			coordenador,
			carteiraOutputPort,
			domainEventDispatcher,
			idempotencyPort);
	}

	@Produces
	@ApplicationScoped
	public ListarTransferenciaInputPort listarTransferenciaUseCase(
		TransferenciaRepository repository) {
		return new ListarTransferenciaUseCase(repository);
	}
}