package com.eliasmeyer.sp.application.usecase.transferencia;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.domain.model.usuario.Email;
import com.eliasmeyer.sp.domain.model.usuario.Lojista;
import com.eliasmeyer.sp.domain.model.usuario.Nome;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioComum;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioFactory;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;
import com.eliasmeyer.sp.domain.shared.DomainEventDispatcher;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Testes do EfetuarTransferenciaUseCase")
class EfetuarTransferenciaUseCaseTest {

	private TransferenciaAutorizadorOutputPort autorizador;
	private TransferenciaOutputPort transferenciaOutputPort;
	private UsuarioOutputPort usuarioOutputPort;
	private DomainEventDispatcher domainEventDispatcher;
	private AppLogger appLogger;

	private EfetuarTransferenciaUseCase useCase;

	private Usuario pagador;
	private Usuario recebedor;

	@BeforeEach
	void setUp() {
		autorizador = mock(TransferenciaAutorizadorOutputPort.class);
		transferenciaOutputPort = mock(TransferenciaOutputPort.class);
		usuarioOutputPort = mock(UsuarioOutputPort.class);
		domainEventDispatcher = mock(DomainEventDispatcher.class);
		appLogger = mock(AppLogger.class);

		useCase = new EfetuarTransferenciaUseCase(
			autorizador,
			transferenciaOutputPort,
			usuarioOutputPort,
			domainEventDispatcher,
			appLogger
		);

		pagador = criarUsuarioComum("12345678909", "pagador@email.com");
		recebedor = criarUsuarioComum("69003525021", "recebedor@email.com");

		pagador.receber(new Dinheiro("500.00"));
	}

	// -------------------------------------------------
	// Cenários de sucesso
	// -------------------------------------------------

	private EfetuarTransferenciaCommand criarCommand(UsuarioId idPagador, UsuarioId idRecebedor,
		String quantia) {
		return new EfetuarTransferenciaCommand(
			idPagador.asString(),
			idRecebedor.asString(),
			new BigDecimal(quantia)
		);
	}

	// -------------------------------------------------
	// Cenários de não autorização
	// -------------------------------------------------

	private UsuarioComum criarUsuarioComum(String cpf, String email) {
		return (UsuarioComum) UsuarioFactory.criar(
			DocumentoFactory.criar(cpf),
			new Nome("Usuario Teste"),
			new Email(email),
			"senhaHash123"
		);
	}

	// -------------------------------------------------
	// Cenários de falha técnica
	// -------------------------------------------------

	private Lojista criarLojista(String cnpj, String email) {
		return (Lojista) UsuarioFactory.criar(
			DocumentoFactory.criar(cnpj),
			new Nome("Lojista Teste"),
			new Email(email),
			"senhaHash123"
		);
	}

	// -------------------------------------------------
	// Cenários de usuário não encontrado
	// -------------------------------------------------

	@Nested
	@DisplayName("Cenários de sucesso")
	class Sucesso {

		@Test
		@DisplayName("Deve realizar transferência quando autorizador aprova")
		void shouldRealizarTransferenciaWhenAutorizadorAprova() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertDoesNotThrow(() -> useCase.execute(command));

			// reserva + estado final = 2 saves
			verify(transferenciaOutputPort, times(2)).salvar(any());
			verify(usuarioOutputPort, times(1)).salvar(pagador);
			verify(usuarioOutputPort, times(1)).salvar(recebedor);
			verify(domainEventDispatcher, times(1)).dispatch(any());
		}

		@Test
		@DisplayName("Deve despachar eventos mesmo quando transferência é realizada")
		void shouldDispatchEventsWhenTransferenciaRealizada() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			useCase.execute(command);

			verify(domainEventDispatcher, times(1)).dispatch(any());
		}
	}

	// -------------------------------------------------
	// Cenários de saldo insuficiente
	// -------------------------------------------------

	@Nested
	@DisplayName("Cenários de não autorização")
	class NaoAutorizacao {

		@Test
		@DisplayName("Deve lançar TransferenciaNaoAutorizadaException quando autorizador rejeita")
		void shouldThrowTransferenciaNaoAutorizadaExceptionWhenAutorizadorRejeita() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(false);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaNaoAutorizadaException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve salvar estado cancelado quando autorizador rejeita")
		void shouldSalvarEstadoCanceladoWhenAutorizadorRejeita() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(false);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaNaoAutorizadaException.class, () -> useCase.execute(command));

			// reserva + cancelada = 2 saves
			verify(transferenciaOutputPort, times(2)).salvar(any());
			verify(domainEventDispatcher, times(1)).dispatch(any());
		}

		@Test
		@DisplayName("Deve lançar TransferenciaIndisponivelException quando autorizador está indisponível")
		void shouldThrowTransferenciaIndisponivelExceptionWhenAutorizadorIndisponivel() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Serviço fora do ar"));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve salvar estado cancelado quando autorizador está indisponível")
		void shouldSalvarEstadoCanceladoWhenAutorizadorIndisponivel() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Serviço fora do ar"));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));

			// reserva + cancelada = 2 saves
			verify(transferenciaOutputPort, times(2)).salvar(any());
			verify(domainEventDispatcher, times(1)).dispatch(any());
		}

		@Test
		@DisplayName("Deve lançar LojistaNaoPodeTransferirDinheiroException quando pagador é lojista")
		void shouldThrowLojistaNaoPodeTransferirDinheiroExceptionWhenPagadorIsLojista() {
			Usuario lojistaPagador = criarLojista("11222333000181", "lojista@email.com");
			lojistaPagador.receber(new com.eliasmeyer.sp.domain.model.carteira.Dinheiro("500.00"));

			when(usuarioOutputPort.buscarPorId(lojistaPagador.getId()))
				.thenReturn(Optional.of(lojistaPagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId()))
				.thenReturn(Optional.of(recebedor));

			EfetuarTransferenciaCommand command = criarCommand(
				lojistaPagador.getId(), recebedor.getId(), "100.00");

			Exception exception = assertThrows(
				TransferenciaIndisponivelException.class, () -> useCase.execute(command));

			assertInstanceOf(TransferenciaIndisponivelException.class, exception);
		}
	}

	// -------------------------------------------------
	// Helpers
	// -------------------------------------------------

	@Nested
	@DisplayName("Cenários de falha técnica")
	class FalhaTecnica {

		@Test
		@DisplayName("Deve lançar TransferenciaIndisponivelException quando BD falha ao salvar a reserva")
		void shouldThrowTransferenciaIndisponivelExceptionWhenBdFalhaAoSalvarReserva() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			doThrow(new RuntimeException("BD indisponível"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Não deve chamar autorizador quando BD falha ao salvar a reserva")
		void shouldNotCallAutorizadorWhenBdFalhaAoSalvarReserva() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			doThrow(new RuntimeException("BD indisponível"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));

			verify(autorizador, never()).isAutorizado(any());
		}

		@Test
		@DisplayName("Deve lançar TransferenciaIndisponivelException quando BD falha ao salvar estado final")
		void shouldThrowTransferenciaIndisponivelExceptionWhenBdFalhaAoSalvarEstadoFinal() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			// primeira chamada (reserva) sucede, segunda (estado final) falha
			doNothing()
				.doThrow(new TransferenciaIndisponivelException("BD indisponível"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve despachar eventos mesmo quando BD falha ao salvar estado final")
		void shouldDispatchEventsEvenWhenBdFalhaAoSalvarEstadoFinal() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			// primeira chamada (reserva) sucede, segunda (estado final) falha
			doNothing()
				.doThrow(new TransferenciaIndisponivelException("BD indisponível"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));

			// finally garante despacho mesmo em falha
			verify(domainEventDispatcher, times(1)).dispatch(any());
		}

		@Test
		@DisplayName("Não deve lançar IllegalStateException quando realizar() já mudou estado para REALIZADA e salvar() falha")
		void shouldNotThrowIllegalStateExceptionWhenRealizadaAndSalvarFails() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			// reserva sucede, salvar do estado REALIZADA falha
			doNothing()
				.doThrow(new TransferenciaIndisponivelException("BD indisponível"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));
		}
	}

	@Nested
	@DisplayName("Cenários de usuário não encontrado")
	class UsuarioNaoEncontrado {

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando pagador não é encontrado")
		void shouldThrowIllegalArgumentExceptionWhenPagadorNaoEncontrado() {
			when(usuarioOutputPort.buscarPorId(any())).thenReturn(Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				new UsuarioId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando recebedor não é encontrado")
		void shouldThrowIllegalArgumentExceptionWhenRecebedorNaoEncontrado() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Não deve salvar transferência quando pagador não é encontrado")
		void shouldNotSalvarTransferenciaWhenPagadorNaoEncontrado() {
			when(usuarioOutputPort.buscarPorId(any())).thenReturn(Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				new UsuarioId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

			verify(transferenciaOutputPort, never()).salvar(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}
	}

	@Nested
	@DisplayName("Cenários de saldo insuficiente")
	class SaldoInsuficiente {

		@Test
		@DisplayName("Deve lançar TransferenciaIndisponivelException quando pagador não tem saldo suficiente")
		void shouldThrowTransferenciaIndisponivelExceptionWhenSaldoInsuficiente() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));

			// quantia maior que o saldo disponível (500.00)
			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "1000.00");

			Exception exception = assertThrows(
				TransferenciaIndisponivelException.class, () -> useCase.execute(command));

			assertInstanceOf(TransferenciaIndisponivelException.class, exception);
		}

		@Test
		@DisplayName("Não deve chamar autorizador quando saldo é insuficiente")
		void shouldNotCallAutorizadorWhenSaldoInsuficiente() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "1000.00");

			assertThrows(TransferenciaIndisponivelException.class, () -> useCase.execute(command));

			verify(autorizador, never()).isAutorizado(any());
		}
	}
}

