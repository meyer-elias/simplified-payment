package com.eliasmeyer.sp.domain.model.carteira;


import com.eliasmeyer.sp.domain.exception.SaldoInsuficienteException;

public class Carteira {

	private final Dinheiro saldoDisponivel;
	private final Dinheiro saldoReservado;

	public Carteira() {
		this.saldoDisponivel = Dinheiro.zero();
		this.saldoReservado = Dinheiro.zero();
	}

	public Dinheiro saldo() {
		return saldoDisponivel;
	}

	public void creditar(Dinheiro quantia) {
		saldoDisponivel.somar(quantia);
	}

	public void reservar(Dinheiro quantia) {
		if (!temSaldo(quantia)) {
			throw new SaldoInsuficienteException("Saldo não disponível para reservar");
		}

		saldoDisponivel.subtrair(quantia);
		saldoReservado.somar(quantia);
	}

	public void confirmarReserva(Dinheiro quantia) {
		saldoReservado.subtrair(quantia);
	}

	/**
	 * Cancela uma reserva, devolvendo a quantia ao saldo disponível.
	 * <p>
	 * Operação idempotente usada tanto para cancelamentos de negócio (autorizador recusa) quanto
	 * para reversões por falha técnica (ex: BD indisponível após reserva). A operação acontece
	 * <strong>em memória</strong>, garantindo que o saldo não fique preso mesmo que a persistência
	 * falhe.
	 *
	 * @param quantia valor a ser devolvido ao saldo disponível
	 */
	public void cancelarReserva(Dinheiro quantia) {
		saldoDisponivel.somar(quantia);
		saldoReservado.subtrair(quantia);
	}

	public boolean temSaldo(Dinheiro quantia) {
		return this.saldoDisponivel.isMaiorOuIgual(quantia);
	}

	public Dinheiro getSaldoDisponivel() {
		return saldoDisponivel;
	}

	public Dinheiro getSaldoReservado() {
		return saldoReservado;
	}
}
