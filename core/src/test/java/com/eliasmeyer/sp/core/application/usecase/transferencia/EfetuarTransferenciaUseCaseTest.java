package com.eliasmeyer.sp.core.application.usecase.transferencia;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.eliasmeyer.sp.core.application.exception.AutorizadorIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaRejeitadaException;
import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraComum;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraFactory;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraLojista;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaCanceladaEvento;
import com.eliasmeyer.sp.core.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Lojista;
import com.eliasmeyer.sp.core.domain.model.usuario.Nome;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioComum;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioFactory;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.core.domain.shared.DomainEvent;
import com.eliasmeyer.sp.core.domain.shared.DomainEventDispatcher;
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

	private TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort;
	private TransferenciaOutputPort transferenciaOutputPort;
	private CarteiraOutputPort carteiraOutputPort;
	private DomainEventDispatcher domainEventDispatcher;
	private AppTransactionManager appTransactionManager;
	private EfetuarTransferenciaUseCase useCase;
	private Carteira pagador;
	private Carteira recebedor;

	@BeforeEach
	void setUp() {
		transferenciaAutorizadorOutputPort = mock(TransferenciaAutorizadorOutputPort.class);
		transferenciaOutputPort = mock(TransferenciaOutputPort.class);
		carteiraOutputPort = mock(CarteiraOutputPort.class);
		domainEventDispatcher = mock(DomainEventDispatcher.class);
		appTransactionManager = mock(AppTransactionManager.class);
		doAnswer(invocation -> {
			Runnable action = invocation.getArgument(0);
			action.run();
			return null;
		}).when(appTransactionManager).execute(any(Runnable.class));
		useCase = new EfetuarTransferenciaUseCase(
			transferenciaAutorizadorOutputPort,
			appTransactionManager,
			domainEventDispatcher,
			carteiraOutputPort,
			transferenciaOutputPort
		);
		pagador = criarCarteiraComum("12345678909", "pagador@email.com");
		recebedor = criarCarteiraComum("69003525021", "recebedor@email.com");
	}

	private EfetuarTransferenciaCommand criarCommand(CarteiraId idPagador, CarteiraId idRecebedor,
		String quantia) {
		return new EfetuarTransferenciaCommand(
			idPagador.asString(),
			idRecebedor.asString(),
			new BigDecimal(quantia)
		);
	}

	private CarteiraComum criarCarteiraComum(String cpf, String email) {
		UsuarioComum usuarioComum = (UsuarioComum) UsuarioFactory.criar(
			DocumentoFactory.criar(cpf),
			new Nome("Usuario Teste"),
			new Email(email),
			"senhaHash123"
		);
		return (CarteiraComum) CarteiraFactory.criar(usuarioComum, new Dinheiro("100.00"));
	}

	private CarteiraLojista criarCarteiraLojista(String cnpj, String email) {
		Lojista lojista = (Lojista) UsuarioFactory.criar(
			DocumentoFactory.criar(cnpj),
			new Nome("Lojista Teste"),
			new Email(email),
			"senhaHash123"
		);
		return (CarteiraLojista) CarteiraFactory.criar(lojista, new Dinheiro("100.00"));
	}

	@Nested
	@DisplayName("Cenarios de sucesso")
	class Sucesso {

		@Test
		@DisplayName("Deve realizar transferencia quando autorizador aprova")
		void shouldRealizarTransferenciaWhenAutorizadorAprova() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertDoesNotThrow(() -> useCase.execute(command));

			verify(transferenciaOutputPort, times(2)).salvar(any());
			verify(carteiraOutputPort, times(2)).salvar(pagador);
			verify(carteiraOutputPort, times(1)).salvar(recebedor);
			verify(domainEventDispatcher, times(1)).dispatch(any());
			verify(appTransactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve despachar eventos ao final quando transferencia e realizada")
		void shouldDispatchEventsWhenTransferenciaRealizada() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);

			useCase.execute(criarCommand(pagador.getId(), recebedor.getId(), "100.00"));

			verify(domainEventDispatcher, times(1)).dispatch(any());
			verify(appTransactionManager, times(2)).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenarios de transferencia para si mesmo")
	class TransferenciaParaSiMesmo {

		@Test
		@DisplayName("Deve lancar TransferenciaRejeitadaException quando pagador e recebedor sao a mesma carteira")
		void shouldThrowTransferenciaRejeitadaExceptionWhenPagadorEqualsRecebedor() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), pagador.getId(), "50.00");

			assertThrows(TransferenciaRejeitadaException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Nao deve chamar autorizador quando pagador e recebedor sao iguais")
		void shouldNotCallAutorizadorWhenPagadorEqualsRecebedor() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), pagador.getId(), "50.00");

			assertThrows(TransferenciaRejeitadaException.class,
				() -> useCase.execute(command));

			verify(transferenciaAutorizadorOutputPort, never()).isAutorizado(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}
	}

	@Nested
	@DisplayName("Cenarios de nao autorizacao")
	class NaoAutorizacao {

		@Test
		@DisplayName("Deve lancar TransferenciaNaoAutorizadaException quando autorizador rejeita")
		void shouldThrowTransferenciaNaoAutorizadaExceptionWhenAutorizadorRejeita() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(false);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaNaoAutorizadaException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve salvar estado cancelado e despachar evento quando autorizador rejeita")
		void shouldSalvarEstadoCanceladoAndDispatchEventWhenAutorizadorRejeita() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(false);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaNaoAutorizadaException.class,
				() -> useCase.execute(command));

			verify(transferenciaOutputPort, times(2)).salvar(any());

			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.forClass(List.class);
			verify(domainEventDispatcher, times(1)).dispatch(eventsCaptor.capture());

			List<DomainEvent> capturedEvents = eventsCaptor.getValue();
			assertFalse(capturedEvents.isEmpty());
			assertTrue(
				capturedEvents.stream().anyMatch(TransferenciaCanceladaEvento.class::isInstance));

			verify(appTransactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve lancar TransferenciaIndisponivelException quando autorizador esta indisponivel")
		void shouldThrowTransferenciaIndisponivelExceptionWhenAutorizadorIndisponivel() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Servico fora do ar"));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve salvar estado cancelado e despachar evento quando autorizador esta indisponivel")
		void shouldSalvarEstadoCanceladoAndDispatchEventWhenAutorizadorIndisponivel() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Servico fora do ar"));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class,
				() -> useCase.execute(command));

			verify(transferenciaOutputPort, times(2)).salvar(any());

			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.forClass(List.class);
			verify(domainEventDispatcher, times(1)).dispatch(eventsCaptor.capture());

			List<DomainEvent> capturedEvents = eventsCaptor.getValue();
			assertTrue(
				capturedEvents.stream().anyMatch(TransferenciaCanceladaEvento.class::isInstance));

			verify(appTransactionManager, times(2)).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenarios de lojista")
	class UsuarioLojista {

		@Test
		@DisplayName("Deve permitir que lojista receba transferencia de usuario comum")
		void shouldPermitirLojistaReceberTransferencia() {
			CarteiraLojista lojistaRecebedor = criarCarteiraLojista("11222333000181",
				"lojista@email.com");
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(lojistaRecebedor.getId())).thenReturn(
				Optional.of(lojistaRecebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), lojistaRecebedor.getId(), "50.00");

			assertDoesNotThrow(() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve lancar TransferenciaRejeitadaException quando lojista tenta transferir")
		void shouldThrowTransferenciaRejeitadaExceptionWhenLojistaTransfere() {
			CarteiraLojista lojistaPagador = criarCarteiraLojista("11222333000181",
				"lojista@email.com");
			when(carteiraOutputPort.buscarPor(lojistaPagador.getId())).thenReturn(
				Optional.of(lojistaPagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));

			EfetuarTransferenciaCommand command = criarCommand(
				lojistaPagador.getId(), recebedor.getId(), "50.00");

			assertThrows(TransferenciaRejeitadaException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Nao deve chamar autorizador quando lojista tenta transferir")
		void shouldNotCallAutorizadorWhenLojistaTransfere() {
			CarteiraLojista lojistaPagador = criarCarteiraLojista("11222333000181",
				"lojista@email.com");
			when(carteiraOutputPort.buscarPor(lojistaPagador.getId())).thenReturn(
				Optional.of(lojistaPagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));

			EfetuarTransferenciaCommand command = criarCommand(
				lojistaPagador.getId(), recebedor.getId(), "50.00");

			assertThrows(TransferenciaRejeitadaException.class,
				() -> useCase.execute(command));

			verify(transferenciaAutorizadorOutputPort, never()).isAutorizado(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}
	}

	@Nested
	@DisplayName("Cenarios de saldo insuficiente")
	class SaldoInsuficiente {

		@Test
		@DisplayName("Deve lancar TransferenciaRejeitadaException quando pagador nao tem saldo suficiente")
		void shouldThrowTransferenciaRejeitadaExceptionWhenSaldoInsuficiente() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "1000.00");

			assertThrows(TransferenciaRejeitadaException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Nao deve chamar autorizador quando pagador nao tem saldo suficiente")
		void shouldNotCallAutorizadorWhenSaldoInsuficiente() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "1000.00");

			assertThrows(TransferenciaRejeitadaException.class,
				() -> useCase.execute(command));

			verify(transferenciaAutorizadorOutputPort, never()).isAutorizado(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}
	}

	@Nested
	@DisplayName("Cenarios de cancelamento")
	class Cancelamento {

		@Test
		@DisplayName("Deve restaurar saldo do pagador quando transferencia e cancelada por nao autorizacao")
		void shouldRestoreSaldoPagadorWhenTransferenciaCanceladaPorNaoAutorizacao() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(false);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "50.00");

			assertThrows(TransferenciaNaoAutorizadaException.class,
				() -> useCase.execute(command));

			verify(carteiraOutputPort, times(2)).salvar(pagador);
			verify(transferenciaOutputPort, times(2)).salvar(any());
		}

		@Test
		@DisplayName("Deve salvar transferencia e carteira pagador quando cancelamento e executado")
		void shouldSalvarTransferenciaAndCarteiraPagadorWhenCancelamentoExecutado() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(false);

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaNaoAutorizadaException.class,
				() -> useCase.execute(command));

			verify(carteiraOutputPort, times(2)).salvar(pagador);
			verify(transferenciaOutputPort, times(2)).salvar(any());
		}
	}

	@Nested
	@DisplayName("Cenarios de despacho de eventos")
	class DespachoDeEventos {

		@Test
		@DisplayName("Nao deve despachar eventos quando falha antes da reserva")
		void shouldNotDispatchEventsWhenFailsBeforeReserva() {
			when(carteiraOutputPort.buscarPor(any(CarteiraId.class))).thenReturn(Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));

			verify(domainEventDispatcher, never()).dispatch(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve despachar evento de cancelamento quando autorizador esta indisponivel")
		void shouldDispatchCancelamentoEventWhenAutorizadorIndisponivel() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any()))
				.thenThrow(new AutorizadorIndisponivelException("Servico fora do ar"));

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(TransferenciaIndisponivelException.class,
				() -> useCase.execute(command));

			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.forClass(List.class);
			verify(domainEventDispatcher, times(1)).dispatch(eventsCaptor.capture());

			List<DomainEvent> capturedEvents = eventsCaptor.getValue();
			assertFalse(capturedEvents.isEmpty());
			assertTrue(
				capturedEvents.stream().anyMatch(TransferenciaCanceladaEvento.class::isInstance));
		}

		@Test
		@DisplayName("Deve despachar exatamente uma vez mesmo quando falhador e acionado")
		void shouldDispatchExactlyOnceEvenWhenFalhadorIsTriggered() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);
			doAnswer(ignored -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(RuntimeException.class, () -> useCase.execute(command));

			verify(domainEventDispatcher, times(1)).dispatch(any());
		}
	}

	@Nested
	@DisplayName("Cenarios de usuario nao encontrado")
	class UsuarioNaoEncontrado {

		@Test
		@DisplayName("Deve lancar IllegalArgumentException quando pagador nao e encontrado")
		void shouldThrowIllegalArgumentExceptionWhenPagadorNaoEncontrado() {
			when(carteiraOutputPort.buscarPor(any(CarteiraId.class))).thenReturn(Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve lancar IllegalArgumentException quando recebedor nao e encontrado")
		void shouldThrowIllegalArgumentExceptionWhenRecebedorNaoEncontrado() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
		}

		@Test
		@DisplayName("Nao deve salvar transferencia quando pagador nao e encontrado")
		void shouldNotSalvarTransferenciaWhenPagadorNaoEncontrado() {
			when(carteiraOutputPort.buscarPor(any(CarteiraId.class))).thenReturn(Optional.empty());

			EfetuarTransferenciaCommand command = criarCommand(
				new CarteiraId(), recebedor.getId(), "100.00");

			assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));

			verify(transferenciaOutputPort, never()).salvar(any());
			verify(domainEventDispatcher, never()).dispatch(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenarios de falha tecnica")
	class FalhaTecnica {

		@Test
		@DisplayName("Deve lancar excecao quando BD falha ao salvar a reserva")
		void shouldThrowExceptionWhenBdFalhaAoSalvarReserva() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(RuntimeException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Nao deve chamar autorizador quando BD falha ao salvar a reserva")
		void shouldNotCallAutorizadorWhenBdFalhaAoSalvarReserva() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(RuntimeException.class, () -> useCase.execute(command));

			verify(transferenciaAutorizadorOutputPort, never()).isAutorizado(any());
			verify(domainEventDispatcher, never()).dispatch(any());
		}

		@Test
		@DisplayName("Deve lancar excecao quando BD falha ao salvar estado final da efetivacao")
		void shouldThrowExceptionWhenBdFalhaAoSalvarEstadoFinal() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);
			doAnswer(ignored -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(RuntimeException.class, () -> useCase.execute(command));
		}

		@Test
		@DisplayName("Deve despachar eventos mesmo quando BD falha ao salvar estado final da efetivacao")
		void shouldDispatchEventsEvenWhenBdFalhaAoSalvarEstadoFinal() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);
			doAnswer(ignored -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			assertThrows(RuntimeException.class, () -> useCase.execute(command));

			verify(domainEventDispatcher, times(1)).dispatch(any());
			verify(appTransactionManager, times(2)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Nao deve lancar IllegalStateException quando realizar() mudou estado para REALIZADA e salvar() falha")
		void shouldNotThrowIllegalStateExceptionWhenRealizadaAndSalvarFails() {
			when(carteiraOutputPort.buscarPor(pagador.getId())).thenReturn(
				Optional.of(pagador));
			when(carteiraOutputPort.buscarPor(recebedor.getId())).thenReturn(
				Optional.of(recebedor));
			when(transferenciaAutorizadorOutputPort.isAutorizado(any())).thenReturn(true);
			doAnswer(ignored -> null)
				.doThrow(new RuntimeException("BD indisponivel"))
				.when(transferenciaOutputPort).salvar(any());

			EfetuarTransferenciaCommand command = criarCommand(
				pagador.getId(), recebedor.getId(), "100.00");

			Exception exception = assertThrows(RuntimeException.class,
				() -> useCase.execute(command));

			assertFalse(exception instanceof IllegalStateException);
		}
	}
}
