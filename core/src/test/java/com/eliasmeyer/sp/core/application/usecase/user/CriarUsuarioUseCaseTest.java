package com.eliasmeyer.sp.core.application.usecase.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eliasmeyer.sp.core.application.exception.RegistradorUsuarioIndisponivelException;
import com.eliasmeyer.sp.core.application.ports.AppTransactionManager;
import com.eliasmeyer.sp.core.domain.model.usuario.Documento;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.ports.in.usuario.CriarUsuarioCommand;
import com.eliasmeyer.sp.core.domain.ports.out.PasswordEncoder;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import com.eliasmeyer.sp.core.domain.ports.out.usuario.UsuarioOutputPort;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Testes do CriarUsuarioUseCase")
class CriarUsuarioUseCaseTest {

	private UsuarioOutputPort usuarioOutputPort;
	private CarteiraOutputPort carteiraOutputPort;
	private PasswordEncoder passwordEncoder;
	private AppTransactionManager appTransactionManager;

	private CriarUsuarioUseCase useCase;

	@BeforeEach
	void setUp() {
		usuarioOutputPort = mock(UsuarioOutputPort.class);
		passwordEncoder = mock(PasswordEncoder.class);
		appTransactionManager = mock(AppTransactionManager.class);
		carteiraOutputPort = mock(CarteiraOutputPort.class);
		doInvocationTransactionHelperMethod();

		useCase = new CriarUsuarioUseCase(usuarioOutputPort, carteiraOutputPort, passwordEncoder,
			appTransactionManager);

		when(passwordEncoder.encode(anyString())).thenReturn("senhaHasheada123");
	}

	private CriarUsuarioCommand criarCommand(String nome, String documento, String email,
		String senha) {
		return new CriarUsuarioCommand(nome, documento, email, senha, "150.00");
	}

	private CriarUsuarioCommand criarCommandValido() {
		return criarCommand("Usuario Teste", "12345678909", "teste@email.com", "senha123");
	}

	private void doInvocationTransactionHelperMethod() {
		doAnswer(invocation -> {
			Runnable action = invocation.getArgument(0);
			action.run();
			return null;
		}).when(appTransactionManager).execute(any(Runnable.class));
	}

	@Nested
	@DisplayName("Cenários de sucesso")
	class Sucesso {

		@Test
		@DisplayName("Deve criar usuário com sucesso quando dados são válidos")
		void shouldCriarUsuarioWhenDadosValidos() {
			CriarUsuarioCommand command = criarCommandValido();

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
			assertDoesNotThrow(() -> useCase.execute(command));

			verify(usuarioOutputPort, times(1)).salvar(any(Usuario.class));
			verify(passwordEncoder, times(1)).encode(command.senha());
			verify(appTransactionManager, times(1)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve criar usuário do tipo comum quando documento é CPF")
		void shouldCriarUsuarioComumWhenDocumentoIsCPF() {
			CriarUsuarioCommand command = criarCommand("Usuario Teste", "12345678909",
				"teste@email.com", "senha123");

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
			assertDoesNotThrow(() -> useCase.execute(command));

			verify(usuarioOutputPort, times(1)).salvar(any(Usuario.class));
			verify(appTransactionManager, times(1)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve criar usuário do tipo lojista quando documento é CNPJ")
		void shouldCriarLojistaWhenDocumentoIsCNPJ() {
			CriarUsuarioCommand command = criarCommand("Lojista Teste", "11444777000161",
				"lojista@email.com", "senha123");

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());

			assertDoesNotThrow(() -> useCase.execute(command));

			verify(usuarioOutputPort, times(1)).salvar(any(Usuario.class));
			verify(appTransactionManager, times(1)).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenários de validação")
	class Validacao {

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando documento é inválido")
		void shouldThrowIllegalArgumentExceptionWhenDocumentoInvalido() {
			CriarUsuarioCommand command = criarCommand("Usuario Teste", "documento-invalido",
				"teste@email.com", "senha123");

			Exception exception = assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
			Assertions.assertThat(exception)
				.hasMessageContaining("Documento inválido", exception.getMessage());

			verify(usuarioOutputPort, never()).salvar(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando email é inválido")
		void shouldThrowIllegalArgumentExceptionWhenEmailInvalido() {
			CriarUsuarioCommand command = criarCommand("Usuario Teste", "12345678909",
				"email-invalido", "senha123");

			Exception exception = assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
			Assertions.assertThat(exception)
				.hasMessageContaining("Email inválido", exception.getMessage());

			verify(usuarioOutputPort, never()).salvar(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando nome é muito curto")
		void shouldThrowIllegalArgumentExceptionWhenNomeMuitoCurto() {
			CriarUsuarioCommand command = criarCommand("AB", "12345678909", "teste@email.com",
				"senha123");

			Exception exception = assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
			assertEquals("Nome deve ter pelo menos 3 caracteres", exception.getMessage());

			verify(usuarioOutputPort, never()).salvar(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenários de duplicidade")
	class Duplicidade {

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando documento já está cadastrado")
		void shouldThrowIllegalArgumentExceptionWhenDocumentoDuplicado() {
			CriarUsuarioCommand command = criarCommandValido();
			Usuario usuarioExistente = mock(Usuario.class);

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.of(usuarioExistente));

			Exception exception = assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
			assertEquals("Documento já cadastrado no sistema", exception.getMessage());

			verify(usuarioOutputPort, never()).buscarPorEmail(any());
			verify(usuarioOutputPort, never()).salvar(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve lançar IllegalArgumentException quando email já está cadastrado")
		void shouldThrowIllegalArgumentExceptionWhenEmailDuplicado() {
			CriarUsuarioCommand command = criarCommandValido();
			Usuario usuarioExistente = mock(Usuario.class);

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(
				Optional.of(usuarioExistente));

			Exception exception = assertThrows(IllegalArgumentException.class,
				() -> useCase.execute(command));
			assertEquals("Email já cadastrado no sistema", exception.getMessage());

			verify(usuarioOutputPort, never()).salvar(any());
			verify(appTransactionManager, never()).execute(any(Runnable.class));
		}
	}

	@Nested
	@DisplayName("Cenários de falha técnica")
	class FalhaTecnica {

		@Test
		@DisplayName("Deve lançar RegistradorUsuarioIndisponivelException quando BD falha ao salvar")
		void shouldThrowRegistradorUsuarioIndisponivelExceptionWhenBdFalha() {
			CriarUsuarioCommand command = criarCommandValido();

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
			doThrow(new RuntimeException("BD indisponível")).when(usuarioOutputPort)
				.salvar(any(Usuario.class));

			Exception exception = assertThrows(RegistradorUsuarioIndisponivelException.class,
				() -> useCase.execute(command));
			assertInstanceOf(RegistradorUsuarioIndisponivelException.class, exception);
			verify(appTransactionManager, times(1)).execute(any(Runnable.class));
		}

		@Test
		@DisplayName("Deve logar erro quando BD falha ao salvar usuário")
		void shouldLogErrorWhenExceptionOcorre() {
			CriarUsuarioCommand command = criarCommandValido();
			RuntimeException excecaoSimulada = new RuntimeException("Erro de conexão");

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
			doThrow(excecaoSimulada).when(usuarioOutputPort).salvar(any(Usuario.class));

			assertThrows(RegistradorUsuarioIndisponivelException.class,
				() -> useCase.execute(command));
			verify(appTransactionManager, times(1)).execute(any(Runnable.class));
		}
	}
}