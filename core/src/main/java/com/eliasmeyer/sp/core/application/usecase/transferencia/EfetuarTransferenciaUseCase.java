package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaRejeitadaException;
import com.eliasmeyer.sp.core.application.ports.TransactionManager;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
import java.util.ArrayList;

/**
 * Use case para efetuar uma transferência de dinheiro entre usuários.
 *
 * @author Elias Meyer
 * @version 1.0
 * @since 1.0
 */
public class EfetuarTransferenciaUseCase implements EfetuarTransferenciaInputPort {

	private final CarteiraOutputPort carteiraOutputPort;

	private final DomainEventDispatcher domainEventDispatcher;

	private final Reservador reservador;

	private final Autorizador autorizador;

	private final Cancelador cancelador;

	private final Efetivador efetivador;

	private final Falhador falhador;

	public EfetuarTransferenciaUseCase(
		TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort,
		TransactionManager transactionManager, DomainEventDispatcher domainEventDispatcher,
		CarteiraOutputPort carteiraOutputPort, TransferenciaOutputPort transferenciaOutputPort) {
		this.domainEventDispatcher = domainEventDispatcher;
		this.carteiraOutputPort = carteiraOutputPort;

		this.cancelador = new Cancelador(transactionManager, carteiraOutputPort,
			transferenciaOutputPort);

		this.autorizador = new Autorizador(transferenciaAutorizadorOutputPort);

		this.reservador = new Reservador(transferenciaOutputPort, carteiraOutputPort,
			transactionManager);

		this.efetivador = new Efetivador(transferenciaOutputPort, carteiraOutputPort,
			transactionManager);

		this.falhador = new Falhador(transactionManager, carteiraOutputPort,
			transferenciaOutputPort);
	}

	@Override
	public void execute(EfetuarTransferenciaCommand command) {
		CarteiraId idPagador = new CarteiraId(command.idPagador());
		CarteiraId idRecebedor = new CarteiraId(command.idRecebedor());
		Dinheiro quantia = new Dinheiro(command.quantia());

		Carteira cPagador = buscarCarteiraPor(idPagador);
		Carteira cRecebedor = buscarCarteiraPor(idRecebedor);

		var transferencia = new Transferencia(cPagador, cRecebedor, quantia);

		// FASE 1: reservar — se falhar aqui (ex: BD fora, saldo insuficiente),
		// o TransactionManager faz rollback automaticamente e a exceção sobe sem passar pelo Falhador.
		reservador.executar(transferencia);
		transferencia.clearEvents();

		try {
			boolean autorizado = autorizador.isAutorizado(transferencia);

			if (!autorizado) {
				throw new TransferenciaNaoAutorizadaException(
					String.format("Usuário [%s] não autorizado para transferência financeira.",
						idPagador));
			}

			efetivador.executar(transferencia);
		} catch (TransferenciaRejeitadaException tie) {
			throw tie;

		} catch (TransferenciaNaoAutorizadaException tnae) {
			cancelador.execute(transferencia);
			throw tnae;

		} catch (AutorizadorIndisponivelException tie) {
			// Pressupõe que qualquer erro do autorizador e como não autorizado!
			cancelador.execute(transferencia);
			throw new TransferenciaIndisponivelException("Serviço Autorizador indisponível", tie);

		} catch (Exception e) {
			// Erros de infraestrutura após a reserva (BD na efetivação, etc.):
			// Falhador reverte a carteira em memória e tenta salvar o estado falhado (best-effort).
			falhador.execute(transferencia);
			throw e;

		} finally {
			// Despacha apenas os eventos acumulados após a reserva (cancelamento, falha ou efetivação).
			domainEventDispatcher.dispatch(new ArrayList<>(transferencia.domainEvents()));
			transferencia.clearEvents();
		}
	}

	private Carteira buscarCarteiraPor(CarteiraId id) {
		return carteiraOutputPort.buscarPor(id).orElseThrow(
			() -> new IllegalArgumentException(
				String.format("Carteira não encontrada: [%s].", id)));
	}
}
