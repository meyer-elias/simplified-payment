package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaIllegalException;
import com.eliasmeyer.sp.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.application.ports.TransactionManager;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.exception.LojistaNaoPodeTransferirDinheiroException;
import com.eliasmeyer.sp.domain.exception.SaldoInsuficienteException;
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

	private final TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort;

	private final TransferenciaOutputPort transferenciaOutputPort;

	private final UsuarioOutputPort usuarioOutputPort;

	private final DomainEventDispatcher domainEventDispatcher;

	private final AppLogger appLogger;

	private final TransactionManager transactionManager;

	public EfetuarTransferenciaUseCase(
		TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort,
		TransferenciaOutputPort transferenciaOutputPort, UsuarioOutputPort usuarioOutputPort,
		DomainEventDispatcher domainEventDispatcher, AppLogger appLogger,
		TransactionManager transactionManager) {
		this.transferenciaAutorizadorOutputPort = transferenciaAutorizadorOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.usuarioOutputPort = usuarioOutputPort;
		this.domainEventDispatcher = domainEventDispatcher;
		this.appLogger = appLogger;
		this.transactionManager = transactionManager;
	}

	@Override
	public void execute(EfetuarTransferenciaCommand command) {
		UsuarioId idPagador = new UsuarioId(command.idPagador());
		UsuarioId idRecebedor = new UsuarioId(command.idRecebedor());
		Dinheiro quantia = new Dinheiro(command.quantia());

		Usuario uPagador = usuarioOutputPort.buscarPorId(idPagador).orElseThrow(
			() -> new IllegalArgumentException(
				String.format("Usuário não encontrado com o id [%s]", idPagador)));

		Usuario uRecebedor = usuarioOutputPort.buscarPorId(idRecebedor).orElseThrow(
			() -> new IllegalArgumentException(
				String.format("Usuário não encontrado com o id [%s]", idRecebedor)));

		var transferencia = new Transferencia(uPagador, uRecebedor, quantia);

		// FASE 1: Reserva dentro da transação — bloqueia saldo no BD antes de qualquer chamada externa
		transactionManager.execute(() -> {
			try {
				transferencia.reservar();
				transferenciaOutputPort.salvar(transferencia);
				usuarioOutputPort.salvar(uPagador);
			} catch (LojistaNaoPodeTransferirDinheiroException | SaldoInsuficienteException e) {
				throw new TransferenciaIllegalException(
					"Transferência inválida por regra de negócio.", e);
			} catch (TransferenciaIllegalException e) {
				throw e;
			} catch (Exception e) {
				if (transferencia.isReservada()) {
					transferencia.cancelar();
					salvarBestEffort(transferencia);
					usuarioOutputPort.salvar(uPagador);
				}
				throw new TransferenciaIndisponivelException("Falha técnica ao reservar.", e);
			}
			return null;
		});

		// FASE 2: Autorizador FORA da transação — chamada HTTP não segura conexão de BD
		boolean autorizado;
		try {
			autorizado = checkAutorizador(idPagador);
		} catch (AutorizadorIndisponivelException e) {
			transferencia.cancelar();
			salvarBestEffort(transferencia);
			throw new TransferenciaIndisponivelException("Serviço do autorizador indisponível", e);
		}

		// FASE 3: Captura — realiza ou cancela dentro de nova transação
		transactionManager.execute(() -> {
			try {
				if (autorizado) {
					transferencia.realizar();
				} else {
					transferencia.cancelar();
				}
				transferenciaOutputPort.salvar(transferencia);
				usuarioOutputPort.salvar(uPagador);
				usuarioOutputPort.salvar(uRecebedor);
			} catch (Exception e) {
				if (transferencia.isReservada()) {
					transferencia.falhar();
				}
				salvarBestEffort(transferencia);
				throw new TransferenciaIndisponivelException("Falha técnica inesperada.", e);
			} finally {
				domainEventDispatcher.dispatch(transferencia.domainEvents());
				transferencia.clearEvents();
			}

			if (transferencia.isCancelada()) {
				throw new TransferenciaNaoAutorizadaException(
					String.format("Usuário [%s] não autorizado para transferir dinheiro.",
						idPagador));
			}
			return null;
		});
	}

	/**
	 * Tenta persistir o estado da transferência sem propagar exceções.
	 * <p>
	 * Usado em blocos de tratamento de erro para registrar o estado final (FALHADA ou CANCELADA)
	 * sem mascarar a exceção original que desencadeou o erro.
	 * </p>
	 */
	private void salvarBestEffort(Transferencia transferencia) {
		try {
			transactionManager.execute(() -> {
				transferenciaOutputPort.salvar(transferencia);
				return null;
			});
		} catch (Exception saveEx) {
			appLogger.error("Error ao gravar no BD", saveEx);
		}
	}

	private boolean checkAutorizador(UsuarioId idPagador) {
		try {
			return transferenciaAutorizadorOutputPort.isAutorizado(idPagador);
		} catch (Exception ex) {
			throw new AutorizadorIndisponivelException("Serviço do autorizador indisponível", ex);
		}
	}
}
