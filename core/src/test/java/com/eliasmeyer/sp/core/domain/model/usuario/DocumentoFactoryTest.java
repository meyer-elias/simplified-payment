package com.eliasmeyer.sp.core.domain.model.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Testes da classe DocumentoFactory")
class DocumentoFactoryTest {

	@Test
	@DisplayName("Deve criar CPF a partir de número com 11 dígitos")
	void shouldCreateCpfFrom11Digits() {
		String cpfValido = "69343565003";

		Documento documento = DocumentoFactory.criar(cpfValido);

		assertNotNull(documento);
		assertInstanceOf(Cpf.class, documento);
		assertEquals("693.435.650-03", documento.getNumero());
	}

	@Test
	@DisplayName("Deve criar CPF a partir de número formatado")
	void shouldCreateCpfFromFormattedNumber() {
		String cpfFormatado = "69343565003";

		Documento documento = DocumentoFactory.criar(cpfFormatado);

		assertNotNull(documento);
		assertInstanceOf(Cpf.class, documento);
		assertEquals("693.435.650-03", documento.getNumero());
	}

	@Test
	@DisplayName("Deve criar CNPJ a partir de número com 14 dígitos")
	void shouldCreateCnpjFrom14Digits() {
		String cnpjValido = "11444777000161";

		Documento documento = DocumentoFactory.criar(cnpjValido);

		assertNotNull(documento);
		assertInstanceOf(Cnpj.class, documento);
		assertEquals("11.444.777/0001-61", documento.getNumero());
	}

	@Test
	@DisplayName("Deve criar CNPJ a partir de número formatado")
	void shouldCreateCnpjFromFormattedNumber() {
		String cnpjFormatado = "11.444.777/0001-61";

		Documento documento = DocumentoFactory.criar(cnpjFormatado);

		assertNotNull(documento);
		assertInstanceOf(Cnpj.class, documento);
		assertEquals("11.444.777/0001-61", documento.getNumero());
	}

	@Test
	@DisplayName("Deve lançar exceção quando número é nulo")
	void shouldThrowExceptionWhenNumberIsNull() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> DocumentoFactory.criar(null),
			"Deveria lançar IllegalArgumentException para número nulo"
		);

		assertTrue(exception.getMessage().contains("obrigatório"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"",
		"   ",
		"\t",
		"\n"
	})
	@DisplayName("Deve lançar exceção quando número é vazio ou em branco")
	void shouldThrowExceptionWhenNumberIsBlank(String numeroEmBranco) {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> DocumentoFactory.criar(numeroEmBranco),
			"Deveria lançar IllegalArgumentException para número em branco"
		);

		assertTrue(exception.getMessage().contains("obrigatório"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"abc",
		"abc.def.ghi-jk",
		"!@#$%",
		"---...///"
	})
	@DisplayName("Deve lançar exceção quando não há dígitos no número")
	void shouldThrowExceptionWhenNoDigitsFound(String numeroSemDigitos) {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> DocumentoFactory.criar(numeroSemDigitos),
			"Deveria lançar IllegalArgumentException quando não há dígitos"
		);

		assertTrue(exception.getMessage().contains("nenhum dígito encontrado"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"123",
		"1234567890",
		"123456789012",
		"123456789012345",
		"1234567890123456"
	})
	@DisplayName("Deve lançar exceção para quantidade de dígitos inválida")
	void shouldThrowExceptionForInvalidDigitCount(String numeroDigitosInvalidos) {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> DocumentoFactory.criar(numeroDigitosInvalidos),
			"Deveria lançar IllegalArgumentException para quantidade inválida de dígitos"
		);

		assertTrue(exception.getMessage().contains("Documento inválido"));
		assertTrue(exception.getMessage().contains("11"));
		assertTrue(exception.getMessage().contains("14"));
	}

	@Test
	@DisplayName("Deve criar documento com caracteres especiais e dígitos misturados")
	void shouldExtractDigitsFromMixedCharacters() {
		String cpfComCaracteresEspeciais = "cpf: 693.435.650-03";

		Documento documento = DocumentoFactory.criar(cpfComCaracteresEspeciais);

		assertNotNull(documento);
		assertInstanceOf(Cpf.class, documento);
		assertEquals("693.435.650-03", documento.getNumero());
	}

	@Test
	@DisplayName("Deve criar CPF removendo espaços")
	void shouldCreateCpfRemovingSpaces() {
		String cpfComEspacos = " 693.435.650-03 ";

		Documento documento = DocumentoFactory.criar(cpfComEspacos);

		assertNotNull(documento);
		assertInstanceOf(Cpf.class, documento);
		assertEquals("693.435.650-03", documento.getNumero());
	}

	@Test
	@DisplayName("Deve criar CNPJ removendo espaços")
	void shouldCreateCnpjRemovingSpaces() {
		String cnpjComEspacos = " 11.444.777/0001-61 ";

		Documento documento = DocumentoFactory.criar(cnpjComEspacos);

		assertNotNull(documento);
		assertInstanceOf(Cnpj.class, documento);
		assertEquals("11.444.777/0001-61", documento.getNumero());
	}
}