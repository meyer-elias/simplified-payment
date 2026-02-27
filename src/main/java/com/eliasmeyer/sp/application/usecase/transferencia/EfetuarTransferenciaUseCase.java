package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaIllegalException;
import com.eliasmeyer.sp.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaNaoAutorizadaException;
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

	public EfetuarTransferenciaUseCase(
		TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort,
		TransferenciaOutputPort transferenciaOutputPort, UsuarioOutputPort usuarioOutputPort,
		DomainEventDispatcher domainEventDispatcher, AppLogger appLogger) {
		this.transferenciaAutorizadorOutputPort = transferenciaAutorizadorOutputPort;
		this.transferenciaOutputPort = transferenciaOutputPort;
		this.usuarioOutputPort = usuarioOutputPort;
		this.domainEventDispatcher = domainEventDispatcher;
		this.appLogger = appLogger;
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

		// 1. Cria e reserva a transferência
		try {
			transferencia.reservar();
			transferenciaOutputPort.salvar(transferencia);
			transferencia.clearEvents();
		} catch (Exception e) {
			// se falhar aqui, nada foi persistido, sem inconsistência
			throw new TransferenciaIndisponivelException(
				String.format("Não foi possível reservar a transferência para o pagador [%s]: %s",
					idPagador, e.getMessage()), e);
		}

		try {
			// 2. Verifica autorização
			boolean autorizado = transferenciaAutorizadorOutputPort.isAutorizado(idPagador);

			if (autorizado) {
				// 3a. Realiza a transferência
				transferencia.realizar();
			} else {
				// 3b. Cancela a reserva
				transferencia.cancelar();
			}

			// 4. Persiste o estado final e salva os usuários (saldos atualizados)
			transferenciaOutputPort.salvar(transferencia);
			usuarioOutputPort.salvar(uPagador);
			usuarioOutputPort.salvar(uRecebedor);
		} catch (AutorizadorIndisponivelException e) {
			// Pressupõe quando indisponível não está autorizado
			transferencia.cancelar();
			salvarBestEffort(transferencia);
			throw new TransferenciaIndisponivelException("Serviço do autorizador indisponível", e);
		} catch (LojistaNaoPodeTransferirDinheiroException | SaldoInsuficienteException e) {
			transferencia.cancelar();
			salvarBestEffort(transferencia);
			throw new TransferenciaIllegalException(
				"Transferência cancelada devido restrição da regra de negócio.", e);
		} catch (Exception e) {
			// Só tenta marcar como FALHADA se o estado ainda for RESERVADA.
			// Se realizar() já foi chamado com sucesso e apenas o salvar() falhou,
			// o estado já é REALIZADA e a transição para FALHADA não é permitida.
			if (transferencia.isReservada()) {
				transferencia.falhar();
			}
			salvarBestEffort(transferencia);
			throw new TransferenciaIndisponivelException("Falha técnica inesperada", e);
		} finally {
			// 5. bloco finally garante que os eventos sejam despachados e limpos em qualquer cenário (sucesso ou erro)
			domainEventDispatcher.dispatch(transferencia.domainEvents());
			transferencia.clearEvents();
		}

		// 6. Lança exceção após o fluxo completo
		if (transferencia.isCancelada()) {
			throw new TransferenciaNaoAutorizadaException(
				String.format("Usuário [%s] não autorizado para transferir dinheiro.", idPagador));
		}
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
			transferenciaOutputPort.salvar(transferencia);
		} catch (Exception saveEx) {
			appLogger.error("Error ao gravar no BD", saveEx);
		}
	}
}
