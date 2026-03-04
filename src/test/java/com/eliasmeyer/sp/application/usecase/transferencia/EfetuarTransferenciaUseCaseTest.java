package com.eliasmeyer.sp.application.usecase.transferencia;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eliasmeyer.sp.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaIllegalException;
import com.eliasmeyer.sp.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.application.ports.TransactionManager;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaCanceladaEvento;
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
import com.eliasmeyer.sp.domain.shared.DomainEvent;
import com.eliasmeyer.sp.domain.shared.DomainEventDispatcher;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@DisplayName("Testes do EfetuarTransferenciaUseCase")
class EfetuarTransferenciaUseCaseTest {

	private TransferenciaAutorizadorOutputPort autorizador;
	private TransferenciaOutputPort transferenciaOutputPort;
	private UsuarioOutputPort usuarioOutputPort;
	private DomainEventDispatcher domainEventDispatcher;
	private TransactionManager transactionManager;
	private EfetuarTransferenciaUseCase useCase;
	private Usuario pagador;
	private Usuario recebedor;

	@BeforeEach
	void setUp() {
		autorizador = mock(TransferenciaAutorizadorOutputPort.class);
		transferenciaOutputPort = mock(TransferenciaOutputPort.class);
		usuarioOutputPort = mock(UsuarioOutputPort.class);
		domainEventDispatcher = mock(DomainEventDispatcher.class);
		transactionManager = mock(TransactionManager.class);
		doInvocationTransactionHelperMethod();
		useCase = new EfetuarTransferenciaUseCase(
			autorizador,
			transactionManager,
			domainEventDispatcher,
			usuarioOutputPort,
			transferenciaOutputPort
		);
		pagador = criarUsuarioComum("12345678909", "pagador@email.com");
		recebedor = criarUsuarioComum("69003525021", "recebedor@email.com");
		pagador.receber(new Dinheiro("500.00"));
	}

	private EfetuarTransferenciaCommand criarCommand(UsuarioId idPagador, UsuarioId idRecebedor,
		String quantia) {
		return new EfetuarTransferenciaCommand(
			idPagador.asString(),
			idRecebedor.asString(),
			new BigDecimal(quantia)
		);
	}

	private UsuarioComum criarUsuarioComum(String cpf, String email) {
		return (UsuarioComum) UsuarioFactory.criar(
			DocumentoFactory.criar(cpf),
			new Nome("Usuario Teste"),
			new Email(email),
			"senhaHash123"
		);
	}

	private Lojista criarLojista(String cnpj, String email) {
		return (Lojista) UsuarioFactory.criar(
			DocumentoFactory.criar(cnpj),
			new Nome("Lojista Teste"),
			new Email(email),
			"senhaHash123"
		);
	}

	private void doInvocationTransactionHelperMethod() {
		doAnswer(invocation -> {
			Runnable action = invocation.getArgument(0);
			action.run();
			return null;
		}).when(transactionManager).execute(any(Runnable.class));
	}

	@Nested
	@DisplayName("Cenarios de sucesso")
	class Sucesso {

		@Test
		@DisplayName("Deve realizar transferencia quando autorizador aprova")
		void shouldRealizarTransferenciaWhenAutorizadorAprova() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");
			assertDoesNotThrow(() -> useCase.execute(command));
			verify(transferenciaOutputPort, times(2)).salvar(any());
			verify(usuarioOutputPort, times(2)).salvar(pagador);
			verify(usuarioOutputPort, times(1)).salvar(recebedor);
			verify(domainEventDispatcher, times(1)).dispatch(any());
			verify(transactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve despachar eventos ao final quando transferencia e realizada")
		void shouldDispatchEventsWhenTransferenciaRealizada() {
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			useCase.execute(criarCommand(pagador.getId(), recebedor.getId(), "100.00"));
			verify(domainEventDispatcher, times(1)).dispatch(any());
			verify(transactionManager, times(2)).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenarios de nao autorizacao")
	class NaoAutorizacao {

		@Test
		@DisplayName("Deve lancar TransferenciaNaoAutorizadaException quando autorizador rejeita")
		void shouldThrowTransferenciaNaoAutorizadaExceptionWhenAutorizadorRejeita() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(false);
			assertThrows(TransferenciaNaoAutorizadaException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
		}

		@Test
		@DisplayName("Deve salvar estado cancelado e despachar evento quando autorizador rejeita")
		void shouldSalvarEstadoCanceladoAndDispatchEventWhenAutorizadorRejeita() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(false);

			assertThrows(TransferenciaNaoAutorizadaException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));

			verify(transferenciaOutputPort, times(2)).salvar(any());

			// Usar ArgumentCaptor para capturar a lista completa
			ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.forClass(List.class);
			verify(domainEventDispatcher, times(1)).dispatch(eventsCaptor.capture());

			List<DomainEvent> capturedEvents = eventsCaptor.getValue();

			assertFalse(capturedEvents.isEmpty(),
				"Event list should not be empty. Events: " + capturedEvents);
			assertTrue(
				capturedEvents.stream().anyMatch(TransferenciaCanceladaEvento.class::isInstance),
				"Expected TransferenciaCanceladaEvento to be dispatched. Events: " + capturedEvents
			);
			verify(transactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve lancar TransferenciaIndisponivelException quando autorizador esta indisponivel")
		void shouldThrowTransferenciaIndisponivelExceptionWhenAutorizadorIndisponivel() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Servico fora do ar"));
			assertThrows(TransferenciaIndisponivelException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
		}

		@Test
		@DisplayName("Deve salvar estado cancelado e despachar evento quando autorizador esta indisponivel")
		void shouldSalvarEstadoCanceladoAndDispatchEventWhenAutorizadorIndisponivel() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Servico fora do ar"));
			assertThrows(TransferenciaIndisponivelException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
			verify(transferenciaOutputPort, times(2)).salvar(any());
			ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.forClass(List.class);
			verify(domainEventDispatcher, times(1)).dispatch(eventsCaptor.capture());
			List<DomainEvent> capturedEvents = eventsCaptor.getValue();
			assertTrue(
				capturedEvents.stream().anyMatch(TransferenciaCanceladaEvento.class::isInstance),
				"Expected TransferenciaCanceladaEvento to be dispatched"
			);
			verify(transactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve lancar TransferenciaIllegalException quando pagador e lojista")
		void shouldThrowTransferenciaIllegalExceptionWhenPagadorIsLojista() {
			Usuario lojistaPagador = criarLojista("11222333000181", "lojista@email.com");
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(
				lojistaPagador.getId(), recebedor.getId(), "100.00");

			lojistaPagador.receber(new Dinheiro("500.00"));

			when(usuarioOutputPort.buscarPorId(lojistaPagador.getId()))
				.thenReturn(Optional.of(lojistaPagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId()))
				.thenReturn(Optional.of(recebedor));

			assertInstanceOf(TransferenciaIllegalException.class,
				assertThrows(TransferenciaIllegalException.class,
					() -> useCase.execute(
						efetuarTransferenciaCommand)));
		}
	}

	@Nested
	@DisplayName("Cenarios de falha tecnica")
	class FalhaTecnica {

		@Test
		@DisplayName("Deve lancar RuntimeException quando BD falha ao salvar a reserva")
		void shouldThrowExceptionWhenBdFalhaAoSalvarReserva() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());
			assertThrows(RuntimeException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
		}

		@Test
		@DisplayName("Nao deve chamar autorizador quando BD falha ao salvar a reserva")
		void shouldNotCallAutorizadorWhenBdFalhaAoSalvarReserva() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());
			assertThrows(RuntimeException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
			verify(autorizador, never()).isAutorizado(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}

		@Test
		@DisplayName("Deve lancar excecao quando BD falha ao salvar estado final da efetivacao")
		void shouldThrowExceptionWhenBdFalhaAoSalvarEstadoFinal() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			doAnswer(invocation -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());
			assertThrows(RuntimeException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
		}

		@Test
		@DisplayName("Deve despachar eventos mesmo quando BD falha ao salvar estado final da efetivacao")
		void shouldDispatchEventsEvenWhenBdFalhaAoSalvarEstadoFinal() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			doAnswer(invocation -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());
			assertThrows(RuntimeException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
			verify(domainEventDispatcher, times(1)).dispatch(any());
			verify(transactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Nao deve lancar IllegalStateException quando realizar() mudou estado para REALIZADA e salvar() falha")
		void shouldNotThrowIllegalStateExceptionWhenRealizadaAndSalvarFails() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(autorizador.isAutorizado(any())).thenReturn(true);
			doAnswer(invocation -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());
			Exception exception = assertThrows(RuntimeException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
			assertInstanceOf(RuntimeException.class, exception);
			assertFalse(exception instanceof IllegalStateException);
		}
	}

	@Nested
	@DisplayName("Cenarios de usuario nao encontrado")
	class UsuarioNaoEncontrado {

		@Test
		@DisplayName("Deve lancar IllegalArgumentException quando pagador nao e encontrado")
		void shouldThrowIllegalArgumentExceptionWhenPagadorNaoEncontrado() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(any())).thenReturn(Optional.empty());
			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
		}

		@Test
		@DisplayName("Deve lancar IllegalArgumentException quando recebedor nao e encontrado")
		void shouldThrowIllegalArgumentExceptionWhenRecebedorNaoEncontrado() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(Optional.empty());
			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
		}

		@Test
		@DisplayName("Nao deve salvar transferencia quando pagador nao e encontrado")
		void shouldNotSalvarTransferenciaWhenPagadorNaoEncontrado() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(new UsuarioId(),
				recebedor.getId(), "100.00");
			when(usuarioOutputPort.buscarPorId(any())).thenReturn(Optional.empty());
			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
			verify(transferenciaOutputPort, never()).salvar(any());
			verify(domainEventDispatcher, never()).dispatch(any());
			verify(transactionManager, never()).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenarios de saldo insuficiente")
	class SaldoInsuficiente {

		@Test
		@DisplayName("Deve lancar TransferenciaIllegalException quando pagador nao tem saldo suficiente")
		void shouldThrowTransferenciaIllegalExceptionWhenSaldoInsuficiente() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "1000.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			assertInstanceOf(TransferenciaIllegalException.class,
				assertThrows(TransferenciaIllegalException.class,
					() -> useCase.execute(efetuarTransferenciaCommand)));
		}

		@Test
		@DisplayName("Nao deve chamar autorizador quando pagador nao tem saldo suficiente")
		void shouldNotCallAutorizadorWhenSaldoInsuficiente() {
			EfetuarTransferenciaCommand efetuarTransferenciaCommand = criarCommand(pagador.getId(),
				recebedor.getId(), "1000.00");
			when(usuarioOutputPort.buscarPorId(pagador.getId())).thenReturn(Optional.of(pagador));
			when(usuarioOutputPort.buscarPorId(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			assertThrows(TransferenciaIllegalException.class,
				() -> useCase.execute(efetuarTransferenciaCommand));
			verify(autorizador, never()).isAutorizado(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}
	}
}
