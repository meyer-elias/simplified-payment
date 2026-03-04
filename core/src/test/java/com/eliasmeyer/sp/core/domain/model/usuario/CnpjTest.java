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

@DisplayName("Testes da classe Cnpj")
class CnpjTest {

	@Test
	@DisplayName("Deve criar um CNPJ válido com sucesso")
	void shouldCreateValidCnpj() {
		// CNPJ válido: 11.222.333/0001-81
		String cnpjValido = "11222333000181";

		Documento cnpj = new Cnpj(cnpjValido);

		assertNotNull(cnpj);
		assertEquals("11.222.333/0001-81", cnpj.getNumero());
	}

	@Test
	@DisplayName("Deve formatar CNPJ corretamente")
	void shouldFormatCnpjCorrectly() {
		// CNPJ válido: 11.444.777/0001-61
		String cnpjValido = "11444777000161";

		Cnpj cnpj = new Cnpj(cnpjValido);

		assertEquals("11.444.777/0001-61", cnpj.getNumero());
	}

	@Test
	@DisplayName("Deve lançar exceção para CNPJ com dígito verificador inválido")
	void shouldThrowExceptionForInvalidCheckDigit() {
		// CNPJ com dígito verificador errado
		String cnpjInvalido = "11222333000182";

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Cnpj(cnpjInvalido),
			"Deveria lançar IllegalArgumentException"
		);

		assertTrue(exception.getMessage().contains("CNPJ inválido"));
		assertTrue(exception.getMessage().contains("dígitos verificadores"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"00000000000000",
		"11111111111111",
		"22222222222222",
		"33333333333333",
		"44444444444444",
		"55555555555555",
		"66666666666666",
		"77777777777777",
		"88888888888888",
		"99999999999999"
	})
	@DisplayName("Deve rejeitar CNPJ com todos os dígitos iguais")
	void shouldRejectCnpjWithAllEqualDigits(String cnpjComDigitosIguais) {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Cnpj(cnpjComDigitosIguais),
			"Deveria rejeitar CNPJ com todos os dígitos iguais"
		);

		assertTrue(exception.getMessage().contains("CNPJ inválido"));
	}

	@Test
	@DisplayName("Deve lançar exceção para CNPJ nulo")
	void shouldThrowExceptionForNullCnpj() {
		assertThrows(
			NullPointerException.class,
			() -> new Cnpj(null),
			"Deveria lançar NullPointerException"
		);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"",
		"123",
		"12345678901",
		"123456789012345"
	})
	@DisplayName("Deve lançar exceção para CNPJ com comprimento incorreto")
	void shouldThrowExceptionForInvalidLength(String cnpjComTamanhoInvalido) {
		assertThrows(
			Exception.class,
			() -> new Cnpj(cnpjComTamanhoInvalido),
			"Deveria lançar exceção para CNPJ com comprimento inválido"
		);
	}

	@Test
	@DisplayName("Deve lançar exceção para CNPJ com caracteres não numéricos")
	void shouldThrowExceptionForNonNumericCnpj() {
		String cnpjComLetras = "1122233300018a";

		assertThrows(
			Exception.class,
			() -> new Cnpj(cnpjComLetras),
			"Deveria lançar exceção para CNPJ com caracteres não numéricos"
		);
	}

	@Test
	@DisplayName("Dois CNPJs iguais devem ser equivalentes")
	void shouldBeEqualForSameCnpj() {
		String cnpjValido = "11222333000181";
		Cnpj cnpj1 = new Cnpj(cnpjValido);
		Cnpj cnpj2 = new Cnpj(cnpjValido);

		assertEquals(cnpj1, cnpj2);
	}

	@Test
	@DisplayName("Dois CNPJs diferentes não devem ser equivalentes")
	void shouldNotBeEqualForDifferentCnpj() {
		Cnpj cnpj1 = new Cnpj("11222333000181");
		Cnpj cnpj2 = new Cnpj("11444777000161");

		assertNotEquals(cnpj1, cnpj2);
	}

	@Test
	@DisplayName("CNPJs iguais devem ter o mesmo hash code")
	void shouldHaveSameHashCodeForEqualCnpj() {
		String cnpjValido = "11222333000181";
		Cnpj cnpj1 = new Cnpj(cnpjValido);
		Cnpj cnpj2 = new Cnpj(cnpjValido);

		assertEquals(cnpj1.hashCode(), cnpj2.hashCode());
	}

	@Test
	@DisplayName("Deve retornar string representativa do objeto")
	void shouldReturnStringRepresentation() {
		Cnpj cnpj = new Cnpj("11222333000181");
		String toString = cnpj.toString();

		assertTrue(toString.contains("Cnpj"));
		assertTrue(toString.contains("11222333000181"));
	}

	@Test
	@DisplayName("Deve validar CNPJ com dígito verificador correto")
	void shouldValidateCheckDigitCorrectly() {
		// CNPJ válido onde o primeiro dígito verificador é 8
		String cnpjValido = "11222333000181";

		Cnpj cnpj = new Cnpj(cnpjValido);
		assertNotNull(cnpj);
	}

	@Test
	@DisplayName("Deve rejeitar CNPJ com primeiro dígito verificador incorreto")
	void shouldRejectCnpjWithInvalidFirstCheckDigit() {
		// Alterando o primeiro dígito verificador
		String cnpjInvalido = "11222333000191";

		assertThrows(
			IllegalArgumentException.class,
			() -> new Cnpj(cnpjInvalido)
		);
	}

	@Test
	@DisplayName("Deve rejeitar CNPJ com segundo dígito verificador incorreto")
	void shouldRejectCnpjWithInvalidSecondCheckDigit() {
		// Alterando o segundo dígito verificador
		String cnpjInvalido = "11222333000182";

		assertThrows(
			IllegalArgumentException.class,
			() -> new Cnpj(cnpjInvalido)
		);
	}

	@Test
	@DisplayName("CNPJ não deve ser igual a objeto de tipo diferente")
	void shouldNotBeEqualToOtherTypes() {
		Cnpj cnpj = new Cnpj("11222333000181");

		assertNotEquals("11222333000181", cnpj);
		assertNotEquals(11222333000181L, cnpj);
		assertNotEquals(null, cnpj);
	}
}