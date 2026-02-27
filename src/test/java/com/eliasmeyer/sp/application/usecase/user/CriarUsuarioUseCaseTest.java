package com.eliasmeyer.sp.application.usecase.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eliasmeyer.sp.application.exception.RegistradorUsuarioIndisponivelException;
import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.model.usuario.Documento;
import com.eliasmeyer.sp.domain.model.usuario.Email;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.ports.in.usuario.CriarUsuarioCommand;
import com.eliasmeyer.sp.domain.ports.out.PasswordEncoder;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Testes do CriarUsuarioUseCase")
class CriarUsuarioUseCaseTest {

	private UsuarioOutputPort usuarioOutputPort;
	private PasswordEncoder passwordEncoder;
	private AppLogger appLogger;

	private CriarUsuarioUseCase useCase;

	@BeforeEach
	void setUp() {
		usuarioOutputPort = mock(UsuarioOutputPort.class);
		passwordEncoder = mock(PasswordEncoder.class);
		appLogger = mock(AppLogger.class);

		useCase = new CriarUsuarioUseCase(usuarioOutputPort, passwordEncoder, appLogger);

		when(passwordEncoder.encode(anyString())).thenReturn("senhaHasheada123");
	}

	private CriarUsuarioCommand criarCommand(String nome, String documento, String email,
		String senha) {
		return new CriarUsuarioCommand(nome, documento, email, senha);
	}

	private CriarUsuarioCommand criarCommandValido() {
		return criarCommand("Usuario Teste", "12345678909", "teste@email.com", "senha123");
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
		}

		@Test
		@DisplayName("Deve criar usuário do tipo lojista quando documento é CNPJ")
		void shouldCriarLojistaWhenDocumentoIsCNPJ() {
			CriarUsuarioCommand command = criarCommand("Lojista Teste", "11222333000181",
				"lojista@email.com", "senha123");

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());

			assertDoesNotThrow(() -> useCase.execute(command));

			verify(usuarioOutputPort, times(1)).salvar(any(Usuario.class));
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

			verify(appLogger, times(1)).error(eq("Erro ao registrar usuário."),
				any(RuntimeException.class));
		}

		@Test
		@DisplayName("Deve logar erro quando ocorre exceção durante registro")
		void shouldLogErrorWhenExceptionOcorre() {
			CriarUsuarioCommand command = criarCommandValido();
			RuntimeException excecaoSimulada = new RuntimeException("Erro de conexão");

			when(usuarioOutputPort.buscarPorDocumento(any(Documento.class))).thenReturn(
				Optional.empty());
			when(usuarioOutputPort.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
			doThrow(excecaoSimulada).when(usuarioOutputPort).salvar(any(Usuario.class));

			assertThrows(RegistradorUsuarioIndisponivelException.class,
				() -> useCase.execute(command));

			verify(appLogger, times(1)).error("Erro ao registrar usuário.", excecaoSimulada);
		}
	}
}