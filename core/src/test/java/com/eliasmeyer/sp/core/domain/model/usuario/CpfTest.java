package com.eliasmeyer.sp.core.domain.model.usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Testes da classe Cpf")
class CpfTest {

	@Test
	@DisplayName("Deve criar um CPF válido com sucesso")
	void shouldCreateValidCpf() {
		// CPF válido: 123.456.789-09
		String cpfValido = "12345678909";

		Documento cpf = new Cpf(cpfValido);

		assertNotNull(cpf);
		assertEquals("123.456.789-09", cpf.getNumero());
	}

	@Test
	@DisplayName("Deve formatar CPF corretamente")
	void shouldFormatCpfCorrectly() {
		String cpfValido = "69343565003";

		Cpf cpf = new Cpf(cpfValido);

		assertEquals("693.435.650-03", cpf.getNumero());
	}

	@Test
	@DisplayName("Deve lançar exceção para CPF com dígito verificador inválido")
	void shouldThrowExceptionForInvalidCheckDigit() {
		// CPF com dígito verificador errado
		String cpfInvalido = "12345678910";

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Cpf(cpfInvalido),
			"Deveria lançar IllegalArgumentException"
		);

		assertTrue(exception.getMessage().contains("CPF inválido"));
		assertTrue(exception.getMessage().contains("dígitos verificadores"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"00000000000",
		"11111111111",
		"22222222222",
		"33333333333",
		"44444444444",
		"55555555555",
		"66666666666",
		"77777777777",
		"88888888888",
		"99999999999"
	})
	@DisplayName("Deve rejeitar CPF com todos os dígitos iguais")
	void shouldRejectCpfWithAllEqualDigits(String cpfComDigitosIguais) {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Cpf(cpfComDigitosIguais),
			"Deveria rejeitar CPF com todos os dígitos iguais"
		);

		assertTrue(exception.getMessage().contains("CPF inválido"));
	}

	@Test
	@DisplayName("Deve lançar exceção para CPF nulo")
	void shouldThrowExceptionForNullCpf() {
		assertThrows(
			NullPointerException.class,
			() -> new Cpf(null),
			"Deveria lançar NullPointerException"
		);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"",
		"123",
		"1234567890",
		"123456789012"
	})
	@DisplayName("Deve lançar exceção para CPF com comprimento incorreto")
	void shouldThrowExceptionForInvalidLength(String cpfComTamanhoInvalido) {
		assertThrows(
			Exception.class,
			() -> new Cpf(cpfComTamanhoInvalido),
			"Deveria lançar exceção para CPF com comprimento inválido"
		);
	}

	@Test
	@DisplayName("Deve lançar exceção para CPF com caracteres não numéricos")
	void shouldThrowExceptionForNonNumericCpf() {
		String cpfComLetras = "1234567890a";

		assertThrows(
			Exception.class,
			() -> new Cpf(cpfComLetras),
			"Deveria lançar exceção para CPF com caracteres não numéricos"
		);
	}

	@Test
	@DisplayName("Dois CPFs iguais devem ser equivalentes")
	void shouldBeEqualForSameCpf() {
		String cpfValido = "12345678909";
		Cpf cpf1 = new Cpf(cpfValido);
		Cpf cpf2 = new Cpf(cpfValido);

		assertEquals(cpf1, cpf2);
	}

	@Test
	@DisplayName("Dois CPFs diferentes não devem ser equivalentes")
	void shouldNotBeEqualForDifferentCpf() {
		Cpf cpf1 = new Cpf("85877951041");
		Cpf cpf2 = new Cpf("69343565003");

		assertNotEquals(cpf1, cpf2);
	}

	@Test
	@DisplayName("CPFs iguais devem ter o mesmo hash code")
	void shouldHaveSameHashCodeForEqualCpf() {
		String cpfValido = "12345678909";
		Cpf cpf1 = new Cpf(cpfValido);
		Cpf cpf2 = new Cpf(cpfValido);

		assertEquals(cpf1.hashCode(), cpf2.hashCode());
	}

	@Test
	@DisplayName("Deve retornar string representativa do objeto")
	void shouldReturnStringRepresentation() {
		Cpf cpf = new Cpf("12345678909");
		String toString = cpf.toString();

		assertTrue(toString.contains("Cpf"));
		assertTrue(toString.contains("12345678909"));
	}

	@Test
	@DisplayName("Deve validar CPF com dígitos correto")
	void shouldValidateCheckDigitCorrectly() {
		// CPF válido onde o primeiro dígito verificador é 0
		String cpfValido = "12345678909";

		Cpf cpf = new Cpf(cpfValido);
		assertNotNull(cpf);
	}

	@Test
	@DisplayName("Deve rejeitar CPF com primeiro dígito verificador incorreto")
	void shouldRejectCpfWithInvalidFirstCheckDigit() {
		// Alterando o primeiro dígito verificador
		String cpfInvalido = "12345678919";

		assertThrows(
			IllegalArgumentException.class,
			() -> new Cpf(cpfInvalido)
		);
	}

	@Test
	@DisplayName("Deve rejeitar CPF com segundo dígito verificador incorreto")
	void shouldRejectCpfWithInvalidSecondCheckDigit() {
		// Alterando o segundo dígito verificador
		String cpfInvalido = "12345678900";

		assertThrows(
			IllegalArgumentException.class,
			() -> new Cpf(cpfInvalido)
		);
	}

	@Test
	@DisplayName("CPF não deve ser igual a objeto de tipo diferente")
	void shouldNotBeEqualToOtherTypes() {
		Cpf cpf = new Cpf("12345678909");

		assertNotEquals("12345678909", cpf);
		assertNotEquals(12345678909L, cpf);
		assertNotEquals(null, cpf);
	}

	@Test
	@DisplayName("CPF e CNPJ não devem ser iguais mesmo com números diferentes")
	void shouldNotBeEqualBetweenDocumentTypes() {
		Cpf cpf = new Cpf("12345678909");

		// Comparação com outro tipo de Documento
		assertNotEquals(new Object(), cpf);
	}

	@Test
	@DisplayName("Deve validar CPF com sequência numérica real e válida")
	void shouldValidateRealCpf() {
		// CPF real válido: 111.444.777-35
		String cpfValido = "11144477735";

		Cpf cpf = new Cpf(cpfValido);
		assertEquals("111.444.777-35", cpf.getNumero());
	}
}