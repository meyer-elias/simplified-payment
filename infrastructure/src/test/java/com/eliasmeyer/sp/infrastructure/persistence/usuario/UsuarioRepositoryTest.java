package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eliasmeyer.sp.core.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Nome;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UsuarioRepositoryTest {

	@Inject
	UsuarioRepository usuarioRepository;

	private Usuario usuarioTeste;

	@BeforeEach
	void setUp() {
		// Cria usuário de teste
		usuarioTeste = UsuarioFactory.criar(
			DocumentoFactory.criar("52998224725"),
			new Nome("Nome de Teste ABC"),
			new Email("teste@example.com"),
			"Usuário Teste"
		);

		// Persiste e comita antes de cada teste
		QuarkusTransaction.requiringNew().run(() ->
			usuarioRepository.salvar(usuarioTeste));
	}

	@Test
	@DisplayName("Deve criar usuário com sucesso")
	@Transactional
	void deveCriarUsuario() {
		// Then
		var resultado = QuarkusTransaction.requiringNew()
			.call(() -> usuarioRepository.buscarPorId(usuarioTeste.getId()));

		assertTrue(resultado.isPresent());
		assertEquals(usuarioTeste.getId(), resultado.get().getId());
		assertEquals(usuarioTeste.getNome(), resultado.get().getNome());
		assertEquals(usuarioTeste.getEmail().address(), resultado.get().getEmail().address());
		assertEquals(usuarioTeste.getDocumento().getNumero(),
			resultado.get().getDocumento().getNumero());
	}

	@Test
	@DisplayName("Não deve criar usuário com email duplicado")
	@Transactional
	void naoDeveCriarUsuarioComEmailDuplicado() {
		var duplicado = UsuarioFactory.criar(
			DocumentoFactory.criar("71428793860"),
			new Nome("Outro Nome"),
			new Email("teste@example.com"), // mesmo email
			"Outro Tipo"
		);

		assertThrows(Exception.class, () ->
			QuarkusTransaction.requiringNew().run(() ->
				usuarioRepository.salvar(duplicado)));
	}

	@Test
	@DisplayName("Não deve criar usuário com documento duplicado")
	@Transactional
	void naoDeveCriarUsuarioComDocumentoDuplicado() {
		var duplicado = UsuarioFactory.criar(
			DocumentoFactory.criar("52998224725"), // mesmo documento
			new Nome("Outro Nome"),
			new Email("outro@example.com"),
			"Outro Tipo"
		);

		assertThrows(Exception.class, () ->
			QuarkusTransaction.requiringNew().run(() ->
				usuarioRepository.salvar(duplicado)));
	}

	@Test
	@DisplayName("Deve buscar usuário por ID")
	@Transactional
	void deveBuscarUsuarioPorId() {
		var usuarioEncontrado = usuarioRepository.buscarPorId(usuarioTeste.getId());

		assertTrue(usuarioEncontrado.isPresent());
		assertEquals(usuarioTeste.getId(), usuarioEncontrado.get().getId());
	}

	@Test
	@DisplayName("Deve retornar vazio ao buscar usuário por ID inexistente")
	void deveRetornarVazioAoBuscarUsuarioPorIdInexistente() {
		var resultado = QuarkusTransaction.requiringNew()
			.call(() -> usuarioRepository.buscarPorId(new UsuarioId()));

		assertFalse(resultado.isPresent());
	}

	@Test
	@DisplayName("Deve buscar usuário por email")
	@Transactional
	void deveBuscarUsuarioPorEmail() {
		var resultado = QuarkusTransaction.requiringNew()
			.call(() -> usuarioRepository.buscarPorEmail(usuarioTeste.getEmail()));

		assertTrue(resultado.isPresent());
		assertEquals(usuarioTeste.getEmail(), resultado.get().getEmail());
	}

	@Test
	@DisplayName("Deve buscar usuário por documento")
	@Transactional
	void deveBuscarUsuarioPorDocumento() {
		var resultado = QuarkusTransaction.requiringNew()
			.call(() -> usuarioRepository.buscarPorDocumento(usuarioTeste.getDocumento()));

		assertTrue(resultado.isPresent());
		assertEquals(usuarioTeste.getDocumento(), resultado.get().getDocumento());
	}

	@Test
	@DisplayName("Deve retornar vazio ao buscar usuário por email inexistente")
	void deveRetornarVazioAoBuscarUsuarioPorEmailInexistente() {
		var resultado = QuarkusTransaction.requiringNew()
			.call(() -> usuarioRepository.buscarPorEmail(new Email("inexistente@example.com")));

		assertFalse(resultado.isPresent());
	}

	@Test
	@DisplayName("Deve retornar vazio ao buscar usuário por documento inexistente")
	void deveRetornarVazioAoBuscarUsuarioPorDocumentoInexistente() {
		var resultado = QuarkusTransaction.requiringNew()
			.call(() -> usuarioRepository.buscarPorDocumento(
				DocumentoFactory.criar("87748248800")));

		assertFalse(resultado.isPresent());
	}

	@AfterEach
	void tearDown() {
		QuarkusTransaction.requiringNew().run(() ->
			usuarioRepository.deleteAll());
	}
}