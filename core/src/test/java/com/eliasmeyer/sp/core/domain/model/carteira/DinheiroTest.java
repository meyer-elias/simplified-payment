package com.eliasmeyer.sp.core.domain.model.carteira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Testes da classe Dinheiro")
class DinheiroTest {

	@Test
	@DisplayName("Deve criar Dinheiro com valor positivo via BigDecimal")
	void shouldCreateDinheiroWithPositiveBigDecimal() {
		BigDecimal valor = new BigDecimal("100.50");

		Dinheiro dinheiro = new Dinheiro(valor);

		assertEquals(new BigDecimal("100.50"), dinheiro.getValor());
	}

	@Test
	@DisplayName("Deve criar Dinheiro com valor positivo via String")
	void shouldCreateDinheiroWithPositiveString() {
		Dinheiro dinheiro = new Dinheiro("250.00");

		assertEquals(new BigDecimal("250.00"), dinheiro.getValor());
	}

	@Test
	@DisplayName("Deve lançar exceção para valor nulo no construtor BigDecimal")
	void shouldThrowExceptionForNullBigDecimal() {
		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> new Dinheiro((BigDecimal) null),
			"Deveria lançar NullPointerException para valor nulo"
		);

		assertTrue(exception.getMessage().contains("nulo"));
	}

	@Test
	@DisplayName("Deve lançar exceção para valor nulo no construtor String")
	void shouldThrowExceptionForNullString() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Dinheiro((String) null),
			"Deveria lançar IllegalArgumentException para String nula"
		);

		assertTrue(exception.getMessage().contains("nulo"));
	}

	@Test
	@DisplayName("Deve lançar exceção para String vazia")
	void shouldThrowExceptionForEmptyString() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Dinheiro(""),
			"Deveria lançar IllegalArgumentException para String vazia"
		);

		assertTrue(exception.getMessage().contains("vazio"));
	}

	@Test
	@DisplayName("Deve lançar exceção para valor negativo via BigDecimal")
	void shouldThrowExceptionForNegativeBigDecimal() {
		BigDecimal valorNegativo = new BigDecimal("-50.00");

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Dinheiro(valorNegativo),
			"Deveria lançar IllegalArgumentException para valor negativo"
		);

		assertTrue(exception.getMessage().contains("não pode ser negativo"));
	}

	@Test
	@DisplayName("Deve lançar exceção para valor negativo via String")
	void shouldThrowExceptionForNegativeString() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new Dinheiro("-100.00"),
			"Deveria lançar IllegalArgumentException para valor negativo"
		);

		assertTrue(exception.getMessage().contains("não pode ser negativo"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"100.555",
		"99.999",
		"0.001"
	})
	@DisplayName("Deve arredondar valor para 2 casas decimais")
	void shouldRoundToTwoDecimalPlaces(String valor) {
		Dinheiro dinheiro = new Dinheiro(valor);

		assertEquals(2, dinheiro.getValor().scale());
	}

	@Test
	@DisplayName("isNegativo deve retornar false para valor positivo")
	void isNegativoShouldReturnFalseForPositive() {
		Dinheiro dinheiro = new Dinheiro("100.00");

		assertFalse(dinheiro.isNegativo());
	}

	@Test
	@DisplayName("isMenorQue deve retornar true quando valor é menor")
	void isMenorQueShouldReturnTrueWhenSmaller() {
		Dinheiro dinheiro1 = new Dinheiro("50.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertTrue(dinheiro1.isMenorQue(dinheiro2));
	}

	@Test
	@DisplayName("isMenorQue deve retornar false quando valor é igual")
	void isMenorQueShouldReturnFalseWhenEqual() {
		Dinheiro dinheiro1 = new Dinheiro("100.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertFalse(dinheiro1.isMenorQue(dinheiro2));
	}

	@Test
	@DisplayName("isMenorQue deve retornar false quando valor é maior")
	void isMenorQueShouldReturnFalseWhenGreater() {
		Dinheiro dinheiro1 = new Dinheiro("150.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertFalse(dinheiro1.isMenorQue(dinheiro2));
	}

	@Test
	@DisplayName("isMaiorOuIgual deve retornar true quando valor é maior")
	void isMaiorOuIgualShouldReturnTrueWhenGreater() {
		Dinheiro dinheiro1 = new Dinheiro("150.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertTrue(dinheiro1.isMaiorOuIgual(dinheiro2));
	}

	@Test
	@DisplayName("isMaiorOuIgual deve retornar true quando valor é igual")
	void isMaiorOuIgualShouldReturnTrueWhenEqual() {
		Dinheiro dinheiro1 = new Dinheiro("100.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertTrue(dinheiro1.isMaiorOuIgual(dinheiro2));
	}

	@Test
	@DisplayName("isMaiorOuIgual deve retornar false quando valor é menor")
	void isMaiorOuIgualShouldReturnFalseWhenSmaller() {
		Dinheiro dinheiro1 = new Dinheiro("50.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertFalse(dinheiro1.isMaiorOuIgual(dinheiro2));
	}

	@Test
	@DisplayName("Deve subtrair valor corretamente")
	void shouldSubtractValueCorrectly() {
		Dinheiro dinheiro = new Dinheiro("100.00");
		Dinheiro quantia = new Dinheiro("30.00");

		dinheiro.subtrair(quantia);

		assertEquals(new BigDecimal("70.00"), dinheiro.getValor());
	}

	@Test
	@DisplayName("Deve lançar exceção ao subtrair valor nulo")
	void shouldThrowExceptionWhenSubtractingNull() {
		Dinheiro dinheiro = new Dinheiro("100.00");

		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> dinheiro.subtrair(null),
			"Deveria lançar NullPointerException ao subtrair nulo"
		);

		assertTrue(exception.getMessage().contains("nulo"));
	}

	@Test
	@DisplayName("Deve somar valor corretamente")
	void shouldAddValueCorrectly() {
		Dinheiro dinheiro = new Dinheiro("50.00");
		Dinheiro quantia = new Dinheiro("25.50");

		dinheiro.somar(quantia);

		assertEquals(new BigDecimal("75.50"), dinheiro.getValor());
	}

	@Test
	@DisplayName("Deve lançar exceção ao somar valor nulo")
	void shouldThrowExceptionWhenAddingNull() {
		Dinheiro dinheiro = new Dinheiro("100.00");

		NullPointerException exception = assertThrows(
			NullPointerException.class,
			() -> dinheiro.somar(null),
			"Deveria lançar NullPointerException ao somar nulo"
		);

		assertTrue(exception.getMessage().contains("nulo"));
	}

	@Test
	@DisplayName("Dois Dinheiros com mesmo valor devem ser iguais")
	void shouldBeEqualForSameValue() {
		Dinheiro dinheiro1 = new Dinheiro("100.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertEquals(dinheiro1, dinheiro2);
	}

	@Test
	@DisplayName("Dois Dinheiros com valores diferentes não devem ser iguais")
	void shouldNotBeEqualForDifferentValues() {
		Dinheiro dinheiro1 = new Dinheiro("100.00");
		Dinheiro dinheiro2 = new Dinheiro("200.00");

		assertNotEquals(dinheiro1, dinheiro2);
	}

	@Test
	@DisplayName("Dinheiros iguais devem ter mesmo hashCode")
	void shouldHaveSameHashCodeForEqualValues() {
		Dinheiro dinheiro1 = new Dinheiro("150.00");
		Dinheiro dinheiro2 = new Dinheiro("150.00");

		assertEquals(dinheiro1.hashCode(), dinheiro2.hashCode());
	}

	@Test
	@DisplayName("compareTo deve retornar negativo quando menor")
	void compareToShouldReturnNegativeWhenSmaller() {
		Dinheiro dinheiro1 = new Dinheiro("50.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertTrue(dinheiro1.compareTo(dinheiro2) < 0);
	}

	@Test
	@DisplayName("compareTo deve retornar positivo quando maior")
	void compareToShouldReturnPositiveWhenGreater() {
		Dinheiro dinheiro1 = new Dinheiro("200.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertTrue(dinheiro1.compareTo(dinheiro2) > 0);
	}

	@Test
	@DisplayName("compareTo deve retornar zero quando igual")
	void compareToShouldReturnZeroWhenEqual() {
		Dinheiro dinheiro1 = new Dinheiro("100.00");
		Dinheiro dinheiro2 = new Dinheiro("100.00");

		assertEquals(0, dinheiro1.compareTo(dinheiro2));
	}

	@Test
	@DisplayName("toString deve conter valor formatado")
	void toStringShouldContainFormattedValue() {
		Dinheiro dinheiro = new Dinheiro("100.50");

		String result = dinheiro.toString();

		assertTrue(result.contains("R$"));
		assertTrue(result.contains("100.50"));
	}

	@Test
	@DisplayName("Deve manter precisão em operações aritméticas")
	void shouldMaintainPrecisionInArithmetic() {
		Dinheiro dinheiro = new Dinheiro("10.00");
		Dinheiro quantia = new Dinheiro("0.001");

		dinheiro.somar(quantia);

		assertEquals(2, dinheiro.getValor().scale());
	}
}
