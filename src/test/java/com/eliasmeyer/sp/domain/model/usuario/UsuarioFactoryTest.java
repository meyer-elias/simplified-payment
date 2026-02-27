package com.eliasmeyer.sp.domain.model.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes da classe UsuarioFactory")
class UsuarioFactoryTest {

	@Test
	@DisplayName("Deve criar UsuarioComum quando documento é CPF")
	void shouldCreateUsuarioComumWhenDocumentIsCpf() {
		Cpf cpf = new Cpf("69003525021");
		Nome nome = new Nome("João Silva");
		Email email = new Email("joao@email.com");
		String senha = "senhaHash123";

		Usuario usuario = UsuarioFactory.criar(cpf, nome, email, senha);

		assertNotNull(usuario);
		assertInstanceOf(UsuarioComum.class, usuario);
		assertTrue(usuario.isComum());
		assertFalse(usuario.isLojista());
		assertEquals(TipoUsuario.COMUM, usuario.getTipo());
		assertEquals(cpf, usuario.getDocumento());
		assertEquals(nome, usuario.getNome());
		assertEquals(email, usuario.getEmail());
		assertEquals(senha, usuario.getSenha());
	}

	@Test
	@DisplayName("Deve criar Lojista quando documento é CNPJ")
	void shouldCreateLojistaWhenDocumentIsCnpj() {
		Cnpj cnpj = new Cnpj("43603788000133");
		Nome nome = new Nome("Empresa XYZ Ltda");
		Email email = new Email("contato@empresa.com");
		String senha = "senhaHash456";

		Usuario usuario = UsuarioFactory.criar(cnpj, nome, email, senha);

		assertNotNull(usuario);
		assertInstanceOf(Lojista.class, usuario);
		assertFalse(usuario.isComum());
		assertTrue(usuario.isLojista());
		assertEquals(TipoUsuario.LOJISTA, usuario.getTipo());
		assertEquals(cnpj, usuario.getDocumento());
		assertEquals(nome, usuario.getNome());
		assertEquals(email, usuario.getEmail());
		assertEquals(senha, usuario.getSenha());
	}

	@Test
	@DisplayName("UsuarioComum pode enviar dinheiro")
	void usuarioComumShouldBeAbleToSendMoney() {
		Cpf cpf = new Cpf("69003525021");
		Nome nome = new Nome("Maria Souza");
		Email email = new Email("maria@email.com");
		String senha = "senhaHash789";

		Usuario usuario = UsuarioFactory.criar(cpf, nome, email, senha);

		assertTrue(usuario.canEnviarDinheiro());
	}

	@Test
	@DisplayName("Lojista não deve poder enviar dinheiro")
	void lojistaShouldNotBeAbleToSendMoney() {
		Cnpj cnpj = new Cnpj("43603788000133");
		Nome nome = new Nome("Loja Exemplo");
		Email email = new Email("loja@exemplo.com");
		String senha = "senhaHash012";

		Usuario usuario = UsuarioFactory.criar(cnpj, nome, email, senha);

		assertFalse(usuario.canEnviarDinheiro());
	}

	@Test
	@DisplayName("Deve lançar exceção para tipo de documento desconhecido")
	void shouldThrowExceptionForUnknownDocumentType() {
		Documento documentoDesconhecido = new Documento("DOC123") {
			@Override
			public String getNumero() {
				return "DOC123";
			}
		};
		Nome nome = new Nome("Teste");
		Email email = new Email("teste@email.com");
		String senha = "senhaHash";

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> UsuarioFactory.criar(documentoDesconhecido, nome, email, senha),
			"Deveria lançar IllegalArgumentException para tipo de documento desconhecido"
		);

		assertTrue(exception.getMessage().contains("Tipo de documento inválido"));
	}

	@Test
	@DisplayName("UsuarioComum deve possuir carteira")
	void usuarioComumShouldHaveCarteira() {
		Cpf cpf = new Cpf("12345678909");
		Nome nome = new Nome("Pedro Santos");
		Email email = new Email("pedro@email.com");
		String senha = "senhaHash345";

		Usuario usuario = UsuarioFactory.criar(cpf, nome, email, senha);

		assertNotNull(usuario.getCarteira());
	}

	@Test
	@DisplayName("Lojista deve possuir carteira")
	void lojistaShouldHaveCarteira() {
		Cnpj cnpj = new Cnpj("11222333000181");
		Nome nome = new Nome("Mercado ABC");
		Email email = new Email("mercado@abc.com");
		String senha = "senhaHash678";

		Usuario usuario = UsuarioFactory.criar(cnpj, nome, email, senha);

		assertNotNull(usuario.getCarteira());
	}
}
