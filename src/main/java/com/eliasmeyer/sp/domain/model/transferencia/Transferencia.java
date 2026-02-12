package com.eliasmeyer.sp.domain.model.transferencia;

import com.eliasmeyer.sp.domain.shared.AggregateRoot;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaCanceladaEvento;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaRealizadaEvento;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaReservadaEvento;
import com.eliasmeyer.sp.domain.model.usuario.User;

import java.time.LocalDateTime;

public class Transferencia extends AggregateRoot<TransferenciaId> {

    private final User pagador;

    private final User recebedor;

    private final Dinheiro quantia;

    private final LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    private TransferenciaState state;

    public Transferencia(User pagador, User recebedor, Dinheiro quantia) {
        super(new TransferenciaId());
        this.pagador = pagador;
        this.recebedor = recebedor;
        this.quantia = quantia;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        this.state = new TransferenciaCriada();
    }

    void mudarState(TransferenciaState state) {
        this.state = state;
    }

    public void reservar() {
        pagador.getCarteira().reservar(this.quantia);
        state.reservar(this);
        atualizadoEm = LocalDateTime.now();
        this.registerEvent(() -> new TransferenciaReservadaEvento(this.getId(), atualizadoEm));
    }

    public void realizar() {
        pagador.getCarteira().confirmarReserva(this.quantia);
        recebedor.getCarteira().creditar(this.quantia);
        state.completar(this);
        atualizadoEm = LocalDateTime.now();
        this.registerEvent(() -> new TransferenciaRealizadaEvento(this.getId(), atualizadoEm));
    }

    public void falhar() {
        state.falhar(this);
        atualizadoEm = LocalDateTime.now();
        this.registerEvent(() -> new TransferenciaCanceladaEvento(this.getId(), atualizadoEm));
    }

    public User getPagador() {
        return pagador;
    }

    public User getRecebedor() {
        return recebedor;
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
}
