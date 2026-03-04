package com.eliasmeyer.sp.core.domain.model.carteira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eliasmeyer.sp.core.domain.exception.SaldoInsuficienteException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes da classe Carteira")
class CarteiraTest {

	@Test
	@DisplayName("Deve criar carteira com saldo zero")
	void shouldCreateCarteiraWithZeroBalance() {
		Carteira carteira = new Carteira();

		assertEquals(BigDecimal.ZERO.setScale(2), carteira.saldo().getValor());
		assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoDisponivel().getValor());
	}

	@Test
	@DisplayName("Deve creditar valor na carteira")
	void shouldCreditValueToCarteira() {
		Carteira carteira = new Carteira();
		Dinheiro quantia = new Dinheiro("100.00");

		carteira.creditar(quantia);

		assertEquals(new BigDecimal("100.00"), carteira.saldo().getValor());
		assertEquals(new BigDecimal("100.00"), carteira.getSaldoDisponivel().getValor());
	}

	@Test
	@DisplayName("Deve creditar múltiplos valores na carteira")
	void shouldCreditMultipleValuesToCarteira() {
		Carteira carteira = new Carteira();

		carteira.creditar(new Dinheiro("50.00"));
		carteira.creditar(new Dinheiro("30.00"));
		carteira.creditar(new Dinheiro("20.00"));

		assertEquals(new BigDecimal("100.00"), carteira.saldo().getValor());
	}

	@Test
	@DisplayName("temSaldo deve retornar true quando há saldo suficiente")
	void temSaldoShouldReturnTrueWhenEnoughBalance() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("100.00"));

		boolean result = carteira.temSaldo(new Dinheiro("50.00"));

		assertTrue(result);
	}

	@Test
	@DisplayName("temSaldo deve retornar true quando saldo é igual")
	void temSaldoShouldReturnTrueWhenBalanceIsEqual() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("100.00"));

		boolean result = carteira.temSaldo(new Dinheiro("100.00"));

		assertTrue(result);
	}

	@Test
	@DisplayName("temSaldo deve retornar false quando não há saldo suficiente")
	void temSaldoShouldReturnFalseWhenNotEnoughBalance() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("50.00"));

		boolean result = carteira.temSaldo(new Dinheiro("100.00"));

		assertFalse(result);
	}

	@Test
	@DisplayName("temSaldo deve retornar false para carteira vazia")
	void temSaldoShouldReturnFalseForEmptyCarteira() {
		Carteira carteira = new Carteira();

		boolean result = carteira.temSaldo(new Dinheiro("1.00"));

		assertFalse(result);
	}

	@Test
	@DisplayName("Deve reservar valor quando há saldo suficiente")
	void shouldReserveValueWhenEnoughBalance() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("100.00"));

		carteira.reservar(new Dinheiro("30.00"));

		assertEquals(new BigDecimal("70.00"), carteira.getSaldoDisponivel().getValor());
	}

	@Test
	@DisplayName("Deve reservar valor igual ao saldo disponível")
	void shouldReserveAllAvailableBalance() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("100.00"));

		carteira.reservar(new Dinheiro("100.00"));

		assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoDisponivel().getValor());
	}

	@Test
	@DisplayName("Deve lançar exceção ao reservar sem saldo suficiente")
	void shouldThrowExceptionWhenReservingWithoutEnoughBalance() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("50.00"));
		Dinheiro quantia100 = new Dinheiro("100.00");

		SaldoInsuficienteException exception = assertThrows(
			SaldoInsuficienteException.class,
			() -> carteira.reservar(quantia100)
		);

		assertTrue(exception.getMessage().contains("Saldo não disponível"));
	}

	@Test
	@DisplayName("Deve lançar exceção ao reservar em carteira vazia")
	void shouldThrowExceptionWhenReservingFromEmptyCarteira() {
		Carteira carteira = new Carteira();
		Dinheiro quantia = new Dinheiro("10.00");

		assertThrows(
			SaldoInsuficienteException.class,
			() -> carteira.reservar(quantia)
		);
	}

	@Test
	@DisplayName("Deve confirmar reserva subtraindo do saldo reservado")
	void shouldConfirmReservationBySubtractingFromReserved() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("100.00"));
		carteira.reservar(new Dinheiro("30.00"));

		carteira.confirmarReserva(new Dinheiro("30.00"));

		assertEquals(new BigDecimal("70.00"), carteira.getSaldoDisponivel().getValor());
	}

	@Test
	@DisplayName("saldo e getSaldoDisponivel devem retornar mesmo valor")
	void saldoAndGetSaldoDisponivelShouldReturnSameValue() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("150.00"));

		assertEquals(carteira.saldo().getValor(), carteira.getSaldoDisponivel().getValor());
	}

	@Test
	@DisplayName("Deve manter precisão decimal em operações")
	void shouldMaintainDecimalPrecision() {
		Carteira carteira = new Carteira();
		carteira.creditar(new Dinheiro("100.555"));

		assertEquals(2, carteira.saldo().getValor().scale());
	}
}