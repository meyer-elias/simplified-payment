package com.eliasmeyer.sp.core.domain.model.carteira;


import com.eliasmeyer.sp.core.domain.model.carteira.exception.SaldoInsuficienteException;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.core.domain.shared.Entity;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Carteira extends Entity<CarteiraId> {

	private final UsuarioId usuarioId;

	private final Dinheiro saldoDisponivel;

	private final Dinheiro saldoReservado;

	private final TipoConta tipoConta;

	private LocalDateTime ultimaAtualizacao;

	protected Carteira(UsuarioId usuarioId, TipoConta tipoConta, Dinheiro saldoInicialDisponivel) {
		super(new CarteiraId());
		this.usuarioId = Objects.requireNonNull(usuarioId, "UsuarioId não pode ser nulo");
		this.tipoConta = tipoConta;
		this.saldoDisponivel = Objects.requireNonNull(saldoInicialDisponivel,
			"saldoInicialDisponivel não pode ser nulo");
		this.saldoReservado = Dinheiro.zero();
		atualizarTimestamp();
	}

	// Construtor para reconstituição
	protected Carteira(CarteiraId carteiraId, UsuarioId usuarioId, TipoConta tipoConta,
		Dinheiro saldoDisponivel, Dinheiro saldoReservado, LocalDateTime ultimaAtualizacao) {
		super(carteiraId);
		this.usuarioId = Objects.requireNonNull(usuarioId, "UsuarioId não pode ser nulo");
		this.tipoConta = tipoConta;
		this.saldoDisponivel = Objects.requireNonNull(saldoDisponivel,
			"saldoDisponivel não pode ser nulo");
		this.saldoReservado = Objects.requireNonNull(saldoReservado,
			"saldoReservado não pode ser nulo");
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public boolean podeEnviarDinheiro() {
		return tipoConta == TipoConta.COMUM;
	}

	public Dinheiro saldo() {
		return saldoDisponivel;
	}

	public void creditar(Dinheiro quantia) {
		saldoDisponivel.somar(quantia);
		atualizarTimestamp();
	}

	public void reservar(Dinheiro quantia) {
		if (!temSaldo(quantia)) {
			throw new SaldoInsuficienteException(
				String.format("Usuário [%s] sem saldo financeiro.", usuarioId));
		}

		saldoDisponivel.subtrair(quantia);
		saldoReservado.somar(quantia);
		atualizarTimestamp();
	}

	public void confirmarReserva(Dinheiro quantia) {
		saldoReservado.subtrair(quantia);
		atualizarTimestamp();
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
		atualizarTimestamp();
	}

	private void atualizarTimestamp() {
		this.ultimaAtualizacao = LocalDateTime.now();
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

	public UsuarioId getUsuarioId() {
		return usuarioId;
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public LocalDateTime getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	@Override
	public final boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
