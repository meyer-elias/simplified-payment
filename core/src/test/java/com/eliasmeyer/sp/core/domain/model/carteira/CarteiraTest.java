package com.eliasmeyer.sp.core.domain.model.carteira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eliasmeyer.sp.core.domain.model.carteira.exception.SaldoInsuficienteException;
import com.eliasmeyer.sp.core.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Nome;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Testes da classe Carteira")
class CarteiraTest {

	private CarteiraComum carteiraComum(String saldoInicial) {
		var usuario = UsuarioFactory.criar(
			DocumentoFactory.criar("12345678909"),
			new Nome("João Silva"),
			new Email("joao@email.com"),
			"senha123"
		);
		return (CarteiraComum) CarteiraFactory.criar(usuario, new Dinheiro(saldoInicial));
	}

	private CarteiraLojista carteiraLojista(String saldoInicial) {
		var usuario = UsuarioFactory.criar(
			DocumentoFactory.criar("11222333000181"),
			new Nome("Loja ABC"),
			new Email("loja@email.com"),
			"senha123"
		);
		return (CarteiraLojista) CarteiraFactory.criar(usuario, new Dinheiro(saldoInicial));
	}

	@Nested
	@DisplayName("Criação")
	class Criacao {

		@Test
		@DisplayName("Deve criar carteira comum com saldo inicial informado")
		void shouldCreateCarteiraComumWithInitialBalance() {
			Carteira carteira = carteiraComum("100.00");

			assertEquals(new BigDecimal("100.00"), carteira.saldo().getValor());
			assertEquals(new BigDecimal("100.00"), carteira.getSaldoDisponivel().getValor());
			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
		}

		@Test
		@DisplayName("Deve criar carteira lojista com saldo inicial informado")
		void shouldCreateCarteiraLojistaWithInitialBalance() {
			Carteira carteira = carteiraLojista("200.00");

			assertEquals(new BigDecimal("200.00"), carteira.saldo().getValor());
			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
		}

		@Test
		@DisplayName("Deve lançar exceção quando saldo inicial é nulo na factory")
		void shouldThrowExceptionWhenSaldoInicialIsNull() {
			var usuario = UsuarioFactory.criar(
				DocumentoFactory.criar("12345678909"),
				new Nome("João Silva"),
				new Email("joao@email.com"),
				"senha123"
			);

			assertThrows(NullPointerException.class,
				() -> CarteiraFactory.criar(usuario, null));
		}

		@Test
		@DisplayName("Deve lançar exceção quando usuario é nulo na factory")
		void shouldThrowExceptionWhenUsuarioIsNull() {
			assertThrows(NullPointerException.class,
				() -> CarteiraFactory.criar(null, new Dinheiro("100.00")));
		}

		@Test
		@DisplayName("Deve retornar usuarioId correto")
		void shouldReturnCorrectUsuarioId() {
			Carteira carteira = carteiraComum("0.00");

			assertNotNull(carteira.getUsuarioId());
		}
	}

	@Nested
	@DisplayName("canEnviarDinheiro")
	class CanEnviarDinheiro {

		@Test
		@DisplayName("Carteira comum pode enviar dinheiro")
		void carteiraComumCanEnviarDinheiro() {
			Carteira carteira = carteiraComum("100.00");

			assertTrue(carteira.canEnviarDinheiro());
		}

		@Test
		@DisplayName("Carteira lojista não pode enviar dinheiro")
		void carteiraLojistaCannotEnviarDinheiro() {
			Carteira carteira = carteiraLojista("100.00");

			assertFalse(carteira.canEnviarDinheiro());
		}
	}

	@Nested
	@DisplayName("Crédito")
	class Credito {

		@Test
		@DisplayName("Deve creditar valor na carteira")
		void shouldCreditValueToCarteira() {
			Carteira carteira = carteiraComum("0.00");

			carteira.creditar(new Dinheiro("100.00"));

			assertEquals(new BigDecimal("100.00"), carteira.saldo().getValor());
			assertEquals(new BigDecimal("100.00"), carteira.getSaldoDisponivel().getValor());
		}

		@Test
		@DisplayName("Deve creditar múltiplos valores na carteira")
		void shouldCreditMultipleValuesToCarteira() {
			Carteira carteira = carteiraComum("0.00");

			carteira.creditar(new Dinheiro("50.00"));
			carteira.creditar(new Dinheiro("30.00"));
			carteira.creditar(new Dinheiro("20.00"));

			assertEquals(new BigDecimal("100.00"), carteira.saldo().getValor());
		}

		@Test
		@DisplayName("Deve manter precisão decimal em operações de crédito")
		void shouldMaintainDecimalPrecisionOnCredit() {
			Carteira carteira = carteiraComum("0.00");

			carteira.creditar(new Dinheiro("100.555"));

			assertEquals(2, carteira.saldo().getValor().scale());
		}
	}

	@Nested
	@DisplayName("temSaldo")
	class TemSaldo {

		@Test
		@DisplayName("Deve retornar true quando há saldo suficiente")
		void temSaldoShouldReturnTrueWhenEnoughBalance() {
			Carteira carteira = carteiraComum("100.00");

			assertTrue(carteira.temSaldo(new Dinheiro("50.00")));
		}

		@Test
		@DisplayName("Deve retornar true quando saldo é exatamente igual")
		void temSaldoShouldReturnTrueWhenBalanceIsEqual() {
			Carteira carteira = carteiraComum("100.00");

			assertTrue(carteira.temSaldo(new Dinheiro("100.00")));
		}

		@Test
		@DisplayName("Deve retornar false quando não há saldo suficiente")
		void temSaldoShouldReturnFalseWhenNotEnoughBalance() {
			Carteira carteira = carteiraComum("50.00");

			assertFalse(carteira.temSaldo(new Dinheiro("100.00")));
		}

		@Test
		@DisplayName("Deve retornar false quando saldo disponível é zero")
		void temSaldoShouldReturnFalseWhenSaldoIsZero() {
			Carteira carteira = carteiraComum("0.00");

			assertFalse(carteira.temSaldo(new Dinheiro("1.00")));
		}
	}

	@Nested
	@DisplayName("Reserva")
	class Reserva {

		@Test
		@DisplayName("Deve reduzir saldo disponível e aumentar saldo reservado ao reservar")
		void shouldReduceDisponivelAndIncreaseReservadoWhenReserving() {
			Carteira carteira = carteiraComum("100.00");

			carteira.reservar(new Dinheiro("30.00"));

			assertEquals(new BigDecimal("70.00"), carteira.getSaldoDisponivel().getValor());
			assertEquals(new BigDecimal("30.00"), carteira.getSaldoReservado().getValor());
		}

		@Test
		@DisplayName("Deve reservar todo o saldo disponível")
		void shouldReserveAllAvailableBalance() {
			Carteira carteira = carteiraComum("100.00");

			carteira.reservar(new Dinheiro("100.00"));

			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoDisponivel().getValor());
			assertEquals(new BigDecimal("100.00"), carteira.getSaldoReservado().getValor());
		}

		@Test
		@DisplayName("Deve lançar SaldoInsuficienteException ao reservar sem saldo suficiente")
		void shouldThrowSaldoInsuficienteExceptionWhenReservingWithoutEnoughBalance() {
			Carteira carteira = carteiraComum("50.00");

			SaldoInsuficienteException exception = assertThrows(
				SaldoInsuficienteException.class,
				() -> carteira.reservar(new Dinheiro("100.00"))
			);

			assertNotNull(exception.getMessage());
		}

		@Test
		@DisplayName("Deve lançar SaldoInsuficienteException ao reservar em carteira zerada")
		void shouldThrowSaldoInsuficienteExceptionWhenReservingFromZeroBalance() {
			Carteira carteira = carteiraComum("0.00");

			assertThrows(
				SaldoInsuficienteException.class,
				() -> carteira.reservar(new Dinheiro("10.00"))
			);
		}

		@Test
		@DisplayName("Não deve alterar saldo reservado quando reserva falha")
		void shouldNotAlterSaldoReservadoWhenReservaFails() {
			Carteira carteira = carteiraComum("50.00");

			assertThrows(SaldoInsuficienteException.class,
				() -> carteira.reservar(new Dinheiro("100.00")));

			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
			assertEquals(new BigDecimal("50.00"), carteira.getSaldoDisponivel().getValor());
		}
	}

	@Nested
	@DisplayName("Confirmar reserva")
	class ConfirmarReserva {

		@Test
		@DisplayName("Deve subtrair do saldo reservado ao confirmar")
		void shouldSubtractFromSaldoReservadoWhenConfirming() {
			Carteira carteira = carteiraComum("100.00");
			carteira.reservar(new Dinheiro("30.00"));

			carteira.confirmarReserva(new Dinheiro("30.00"));

			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
			assertEquals(new BigDecimal("70.00"), carteira.getSaldoDisponivel().getValor());
		}

		@Test
		@DisplayName("Deve manter saldo disponível inalterado ao confirmar reserva")
		void shouldKeepSaldoDisponivelUnchangedWhenConfirmingReserva() {
			Carteira carteira = carteiraComum("100.00");
			carteira.reservar(new Dinheiro("40.00"));

			carteira.confirmarReserva(new Dinheiro("40.00"));

			assertEquals(new BigDecimal("60.00"), carteira.getSaldoDisponivel().getValor());
		}
	}

	@Nested
	@DisplayName("Cancelar reserva")
	class CancelarReserva {

		@Test
		@DisplayName("Deve devolver quantia ao saldo disponível ao cancelar reserva")
		void shouldReturnQuantiaToDisponivelWhenCancellingReserva() {
			Carteira carteira = carteiraComum("100.00");
			carteira.reservar(new Dinheiro("40.00"));

			carteira.cancelarReserva(new Dinheiro("40.00"));

			assertEquals(new BigDecimal("100.00"), carteira.getSaldoDisponivel().getValor());
			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
		}

		@Test
		@DisplayName("Deve zerar saldo reservado ao cancelar reserva total")
		void shouldZeroSaldoReservadoWhenCancellingFullReserva() {
			Carteira carteira = carteiraComum("100.00");
			carteira.reservar(new Dinheiro("100.00"));

			carteira.cancelarReserva(new Dinheiro("100.00"));

			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
			assertEquals(new BigDecimal("100.00"), carteira.getSaldoDisponivel().getValor());
		}

		@Test
		@DisplayName("Deve restaurar saldo disponível parcialmente ao cancelar reserva parcial")
		void shouldPartiallyRestoreDisponivelWhenCancellingPartialReserva() {
			Carteira carteira = carteiraComum("100.00");
			carteira.reservar(new Dinheiro("60.00"));

			carteira.cancelarReserva(new Dinheiro("30.00"));

			assertEquals(new BigDecimal("70.00"), carteira.getSaldoDisponivel().getValor());
			assertEquals(new BigDecimal("30.00"), carteira.getSaldoReservado().getValor());
		}
	}

	@Nested
	@DisplayName("saldo e getSaldoDisponivel")
	class SaldoConsistencia {

		@Test
		@DisplayName("saldo e getSaldoDisponivel devem retornar mesmo valor")
		void saldoAndGetSaldoDisponivelShouldReturnSameValue() {
			Carteira carteira = carteiraComum("150.00");

			assertEquals(carteira.saldo().getValor(), carteira.getSaldoDisponivel().getValor());
		}

		@Test
		@DisplayName("Saldo total não deve mudar ao reservar e cancelar")
		void saldoTotalShouldNotChangeAfterReservaAndCancelamento() {
			Carteira carteira = carteiraComum("100.00");
			carteira.reservar(new Dinheiro("50.00"));

			carteira.cancelarReserva(new Dinheiro("50.00"));

			assertEquals(new BigDecimal("100.00"), carteira.getSaldoDisponivel().getValor());
			assertEquals(BigDecimal.ZERO.setScale(2), carteira.getSaldoReservado().getValor());
		}
	}
}