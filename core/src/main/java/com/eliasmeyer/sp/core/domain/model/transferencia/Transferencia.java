package com.eliasmeyer.sp.core.domain.model.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaCanceladaEvento;
import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaFalhadaEvento;
import com.eliasmeyer.sp.core.domain.model.transferencia.eventos.TransferenciaRealizadaEvento;
import com.eliasmeyer.sp.core.domain.model.transferencia.exception.LojistaNaoPodeTransferirDinheiroException;
import com.eliasmeyer.sp.core.domain.model.transferencia.exception.TransferenciaParaSiMesmoException;
import com.eliasmeyer.sp.core.domain.model.transferencia.exception.TransferenciaQuantiaInvalidaException;
import com.eliasmeyer.sp.core.domain.shared.AggregateRoot;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transferencia extends AggregateRoot<TransferenciaId> {

	private final Carteira carteiraPagador;

	private final Carteira carteiraRecebedor;

	private final Dinheiro quantia;

	private final LocalDateTime criadoEm;

	private LocalDateTime atualizadoEm;

	private TransferenciaState state;

	private TransferenciaStatus status;

	public Transferencia(Carteira carteiraPagador,
		Carteira carteiraRecebedor, Dinheiro quantia) {
		super(new TransferenciaId());
		this.carteiraPagador = carteiraPagador;
		this.carteiraRecebedor = carteiraRecebedor;
		this.quantia = quantia;
		this.criadoEm = LocalDateTime.now();
		this.atualizadoEm = LocalDateTime.now();
		this.state = new TransferenciaCriada();
		this.status = TransferenciaStatus.CRIADA;
	}

	Transferencia(TransferenciaReconstituicao reconstituicao) {
		super(reconstituicao.id());
		this.carteiraPagador = reconstituicao.carteiraPagador();
		this.carteiraRecebedor = reconstituicao.carteiraRecebedor();
		this.quantia = reconstituicao.quantia();
		this.criadoEm = reconstituicao.criadoEm();
		this.atualizadoEm = reconstituicao.atualizadoEm();
		this.state = resolverState(reconstituicao.status());
		this.status = reconstituicao.status();
	}

	private TransferenciaState resolverState(TransferenciaStatus status) {
		return switch (status) {
			case CRIADA -> new TransferenciaCriada();
			case RESERVADA -> new TransferenciaReservada();
			case REALIZADA -> new TransferenciaRealizada();
			case CANCELADA -> new TransferenciaCancelada();
			case FALHADA -> new TransferenciaFalhada();
		};
	}

	void mudarState(TransferenciaState state) {
		this.state = state;
		this.status = resolverStatus(state);
	}

	private TransferenciaStatus resolverStatus(TransferenciaState state) {
		return switch (state) {
			case TransferenciaCriada _ -> TransferenciaStatus.CRIADA;
			case TransferenciaReservada _ -> TransferenciaStatus.RESERVADA;
			case TransferenciaRealizada _ -> TransferenciaStatus.REALIZADA;
			case TransferenciaCancelada _ -> TransferenciaStatus.CANCELADA;
			case TransferenciaFalhada _ -> TransferenciaStatus.FALHADA;
			default -> throw new IllegalStateException("Estado desconhecido: " + state.getClass());
		};
	}

	public void reservar() {
		if (this.carteiraPagador.equals(carteiraRecebedor)) {
			throw new TransferenciaParaSiMesmoException(
				String.format(
					"Não é permitido transação de transferência [%s] para si mesmo.", this.id));
		}

		if (quantia.isZero()) {
			throw new TransferenciaQuantiaInvalidaException(
				String.format("Transação de transferência [id=%s] precisa de valor maior que zero.",
					this.id));
		}

		if (!carteiraPagador.podeEnviarDinheiro()) {
			throw new LojistaNaoPodeTransferirDinheiroException(String.format(
				"Lojistas [%s] não podem realizar cessão de valor na transação de transferência [id=%s].",
				carteiraPagador.getUsuarioId(), this.id));
		}
		carteiraPagador.reservar(this.quantia);
		state.reservar(this);
		atualizadoEm = LocalDateTime.now();
	}

	public void realizar() {
		carteiraPagador.confirmarReserva(this.quantia);
		carteiraRecebedor.creditar(this.quantia);
		state.completar(this);
		atualizadoEm = LocalDateTime.now();
		this.registerEvent(() -> new TransferenciaRealizadaEvento(this));
	}

	public void cancelar() {
		carteiraPagador.cancelarReserva(this.quantia);
		state.cancelar(this);
		atualizadoEm = LocalDateTime.now();
		this.registerEvent(() -> new TransferenciaCanceladaEvento(this));
	}

	public void falhar() {
		// Cancela a reserva da carteira em memória independente do BD,
		// pois se chegamos aqui o dinheiro ainda está bloqueado no pagador.
		if (isReservada()) {
			carteiraPagador.cancelarReserva(this.quantia);
		}
		state.falhar(this);
		atualizadoEm = LocalDateTime.now();
		this.registerEvent(() -> new TransferenciaFalhadaEvento(this));
	}

	public boolean isReservada() {
		return state instanceof TransferenciaReservada;
	}

	public Carteira getPagador() {
		return carteiraPagador;
	}

	public Carteira getRecebedor() {
		return carteiraRecebedor;
	}

	public Dinheiro getQuantia() {
		return quantia;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public TransferenciaStatus getStatus() {
		return status;
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Transferencia that)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return 31 * Objects.hashCode(this.id);
	}
}
