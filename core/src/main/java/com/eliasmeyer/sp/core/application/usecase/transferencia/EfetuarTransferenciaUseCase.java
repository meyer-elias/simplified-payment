package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaRejeitadaException;
import com.eliasmeyer.sp.core.application.ports.out.IdempotencyPort;
import com.eliasmeyer.sp.core.application.usecase.transferencia.services.TransferenciaCoordenador;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Use case para efetuar uma transferência de dinheiro entre usuários.
 *
 * @author Elias Meyer
 * @version 1.0
 * @since 1.0
 */
public class EfetuarTransferenciaUseCase implements EfetuarTransferenciaInputPort {

	private final TransferenciaCoordenador coordenador;

	private final TransferenciaAutorizadorOutputPort autorizador;

	private final CarteiraOutputPort carteiraOutputPort;

	private final IdempotencyPort idempotencyPort;

	private final DomainEventDispatcher domainEventDispatcher;

	public EfetuarTransferenciaUseCase(
		TransferenciaAutorizadorOutputPort autorizador,
		TransferenciaCoordenador coordenador,
		CarteiraOutputPort carteiraOutputPort,
		DomainEventDispatcher domainEventDispatcher,
		IdempotencyPort idempotencyPort) {
		this.domainEventDispatcher = domainEventDispatcher;
		this.coordenador = coordenador;
		this.carteiraOutputPort = carteiraOutputPort;
		this.autorizador = autorizador;
		this.idempotencyPort = idempotencyPort;
	}

	@Override
	public void execute(EfetuarTransferenciaCommand command) {

		if (Objects.nonNull(command.idempotencyKey()) &&
			idempotencyPort.jaProcessado(command.idempotencyKey())) {
			return;
		}

		CarteiraId idPagador = new CarteiraId(command.idPagador());
		CarteiraId idRecebedor = new CarteiraId(command.idRecebedor());
		Dinheiro quantia = new Dinheiro(command.quantia());

		Carteira cPagador = buscarCarteiraPor(idPagador);
		Carteira cRecebedor = buscarCarteiraPor(idRecebedor);

		var transferencia = new Transferencia(cPagador, cRecebedor, quantia);

		// FASE 1: reservar — se falhar aqui (ex: BD fora, saldo insuficiente),
		// o TransactionManager faz rollback automaticamente e a exceção sobe sem passar pelo Falhador.
		coordenador.reservar(transferencia);
		transferencia.clearEvents();

		boolean sucesso = false;
		try {
			boolean autorizado = autorizador.isAutorizado(
				transferencia.getPagador().getUsuarioId());

			if (!autorizado) {
				throw new TransferenciaNaoAutorizadaException(
					String.format("Usuário [%s] não autorizado para transferência financeira.",
						idPagador));
			}

			coordenador.efetivar(transferencia);
			sucesso = true;
		} catch (TransferenciaRejeitadaException tie) {
			throw tie;

		} catch (TransferenciaNaoAutorizadaException tnae) {
			coordenador.cancelar(transferencia);
			throw tnae;

		} catch (AutorizadorIndisponivelException tie) {
			// Pressupõe que qualquer erro do autorizador e como não autorizado!
			coordenador.cancelar(transferencia);
			throw new TransferenciaIndisponivelException("Serviço Autorizador indisponível", tie);

		} catch (Exception e) {
			// Erros de infraestrutura após a reserva (BD na efetivação, etc.):
			// Falhador reverte a carteira em memória e tenta salvar o estado falhado (best-effort).
			coordenador.falhar(transferencia);
			throw e;

		} finally {
			// Despacha apenas os eventos acumulados após a reserva (cancelamento, falha ou efetivação).
			domainEventDispatcher.dispatch(new ArrayList<>(transferencia.domainEvents()));
			transferencia.clearEvents();

			if (sucesso && Objects.nonNull(command.idempotencyKey())) {
				idempotencyPort.registrar(command.idempotencyKey());
			}
		}
	}

	private Carteira buscarCarteiraPor(CarteiraId id) {
		return carteiraOutputPort.buscarPor(id).orElseThrow(
			() -> new IllegalArgumentException(
				String.format("Carteira não encontrada: [%s].", id)));
	}
}
