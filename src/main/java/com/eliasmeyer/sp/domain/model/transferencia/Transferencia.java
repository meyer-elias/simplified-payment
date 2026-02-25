package com.eliasmeyer.sp.domain.model.transferencia;

import com.eliasmeyer.sp.domain.exception.LojistaNaoPodeTransferirDinheiroException;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaCanceladaEvento;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaRealizadaEvento;
import com.eliasmeyer.sp.domain.model.transferencia.eventos.TransferenciaReservadaEvento;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.shared.AggregateRoot;
import java.time.LocalDateTime;

public class Transferencia extends AggregateRoot<TransferenciaId> {

  private final Usuario pagador;

  private final Usuario recebedor;

  private final Dinheiro quantia;

  private final LocalDateTime criadoEm;

  private LocalDateTime atualizadoEm;

  private TransferenciaState state;

  public Transferencia(Usuario pagador, Usuario recebedor, Dinheiro quantia) {
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
    if (!pagador.canEnviarDinheiro()) {
      falhar();
      throw new LojistaNaoPodeTransferirDinheiroException("Lojista nao pode transferir dinheiro");
    }
    pagador.getCarteira().reservar(this.quantia);
    state.reservar(this);
    atualizadoEm = LocalDateTime.now();
    this.registerEvent(() -> new TransferenciaReservadaEvento(this, atualizadoEm));
  }

  public void realizar() {
    pagador.getCarteira().confirmarReserva(this.quantia);
    recebedor.getCarteira().creditar(this.quantia);
    state.completar(this);
    atualizadoEm = LocalDateTime.now();
    this.registerEvent(() -> new TransferenciaRealizadaEvento(this, atualizadoEm));
  }

  public void falhar() {
    state.falhar(this);
    atualizadoEm = LocalDateTime.now();
    this.registerEvent(() -> new TransferenciaCanceladaEvento(this, atualizadoEm));
  }

  public Usuario getPagador() {
    return pagador;
  }

  public Usuario getRecebedor() {
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
