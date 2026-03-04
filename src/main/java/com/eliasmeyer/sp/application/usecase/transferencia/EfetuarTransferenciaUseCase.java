package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaIllegalException;
import com.eliasmeyer.sp.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.application.ports.TransactionManager;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;
import com.eliasmeyer.sp.domain.shared.DomainEventDispatcher;

public class EfetuarTransferenciaUseCase implements EfetuarTransferenciaInputPort {

	private final UsuarioOutputPort usuarioOutputPort;

	private final DomainEventDispatcher domainEventDispatcher;

	private final AppLogger appLogger;

	private final Reservador reservador;

	private final Autorizador autorizador;

	private final Cancelador cancelador;

	private final Efetivador efetivador;

	private final Falhador falhador;

	public EfetuarTransferenciaUseCase(
		TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort, AppLogger appLogger,
		TransactionManager transactionManager, DomainEventDispatcher domainEventDispatcher,
		UsuarioOutputPort usuarioOutputPort, TransferenciaOutputPort transferenciaOutputPort) {
		this.appLogger = appLogger;
		this.domainEventDispatcher = domainEventDispatcher;
		this.usuarioOutputPort = usuarioOutputPort;

		this.cancelador = new Cancelador(transactionManager, usuarioOutputPort,
			transferenciaOutputPort);
		this.autorizador = new Autorizador(transferenciaAutorizadorOutputPort, appLogger);

		this.reservador = new Reservador(transferenciaOutputPort, usuarioOutputPort,
			transactionManager, appLogger);
		this.efetivador = new Efetivador(transferenciaOutputPort, usuarioOutputPort,
			transactionManager);

		this.falhador = new Falhador(transactionManager, usuarioOutputPort,
			transferenciaOutputPort, appLogger);
	}

	@Override
	public void execute(EfetuarTransferenciaCommand command) {
		UsuarioId idPagador = new UsuarioId(command.idPagador());
		UsuarioId idRecebedor = new UsuarioId(command.idRecebedor());
		Dinheiro quantia = new Dinheiro(command.quantia());

		Usuario uPagador = buscarUsuarioPorId(idPagador);
		Usuario uRecebedor = buscarUsuarioPorId(idRecebedor);

		var transferencia = new Transferencia(uPagador, uRecebedor, quantia);

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
		} catch (TransferenciaIllegalException tie) {
			appLogger.error("Transferência inválida", tie);
			throw tie;

		} catch (TransferenciaNaoAutorizadaException tnae) {
			appLogger.error("Transferência não autorizada", tnae);
			cancelador.execute(transferencia);
			throw tnae;

		} catch (AutorizadorIndisponivelException tie) {
			// Pressupõe que qualquer erro do autorizador e como não autorizado!
			cancelador.execute(transferencia);
			appLogger.error("Transferência indisponível", tie);
			throw new TransferenciaIndisponivelException("Serviço Autorizador indisponível", tie);

		} catch (Exception e) {
			// Erros de infraestrutura após a reserva (BD na efetivação, etc.):
			// Falhador reverte a carteira em memória e tenta salvar o estado falhado (best-effort).
			appLogger.error("Erro inesperado ao executar transferência", e);
			falhador.execute(transferencia);
			throw e;

		} finally {
			// Despacha apenas os eventos acumulados após a reserva (cancelamento, falha ou efetivação).
			domainEventDispatcher.dispatch(transferencia.domainEvents());
			transferencia.clearEvents();
		}
	}

	private Usuario buscarUsuarioPorId(UsuarioId id) {
		return usuarioOutputPort.buscarPorId(id).orElseThrow(
			() -> new IllegalArgumentException(
				String.format("Usuário não encontrado com o id [%s]", id)));
	}
}
