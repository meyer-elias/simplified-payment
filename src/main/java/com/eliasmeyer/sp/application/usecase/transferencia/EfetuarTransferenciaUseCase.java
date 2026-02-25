package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.shared.DefaultDomainEventDispatcher;
import com.eliasmeyer.sp.domain.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaAutorizadorOutputPort;
import com.eliasmeyer.sp.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.domain.ports.out.usuario.UsuarioOutputPort;

public class EfetuarTransferenciaUseCase implements EfetuarTransferenciaInputPort {

  private final TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort;

  private final TransferenciaOutputPort transferenciaOutputPort;

  private final UsuarioOutputPort usuarioOutputPort;

  private final DefaultDomainEventDispatcher eventDispatcher;

  public EfetuarTransferenciaUseCase(
      TransferenciaAutorizadorOutputPort transferenciaAutorizadorOutputPort,
      TransferenciaOutputPort transferenciaOutputPort, UsuarioOutputPort usuarioOutputPort,
      DefaultDomainEventDispatcher eventDispatcher) {
    this.transferenciaAutorizadorOutputPort = transferenciaAutorizadorOutputPort;
    this.transferenciaOutputPort = transferenciaOutputPort;
    this.usuarioOutputPort = usuarioOutputPort;
    this.eventDispatcher = eventDispatcher;
  }

  @Override
  public void execute(EfetuarTransferenciaCommand command) {
    UsuarioId idPagador = new UsuarioId(command.IdPagador());
    UsuarioId idRecebedor = new UsuarioId(command.IdRecebedor());
    Dinheiro quantia = new Dinheiro(command.quantia());

    Usuario uPagador = usuarioOutputPort.buscarPorId(idPagador).orElseThrow(
        () -> new IllegalArgumentException(
            String.format("Usuário não encontrado com o id [%s]", idPagador)));

    Usuario uRecebedor = usuarioOutputPort.buscarPorId(idRecebedor).orElseThrow(
        () -> new IllegalArgumentException(
            String.format("Usuário não encontrado com o id [%s]", idRecebedor)));

    var transferencia = new Transferencia(uPagador, uRecebedor, quantia);

    try {
      transferenciaOutputPort.salvar(transferencia);

      if (!transferenciaAutorizadorOutputPort.isAutorizado(idPagador)) {
        transferencia.falhar();
        transferenciaOutputPort.salvar(transferencia);
        eventDispatcher.dispatch(transferencia.domainEvents());
        transferencia.clearEvents();
        usuarioOutputPort.salvar(uPagador);
        usuarioOutputPort.salvar(uRecebedor);
        throw new TransferenciaNaoAutorizadaException(
            String.format("Usuário [%s] não autorizado para transferir dinheiro.", idPagador));
      }

      transferencia.realizar();
      transferenciaOutputPort.salvar(transferencia);
      usuarioOutputPort.salvar(uPagador);
      usuarioOutputPort.salvar(uRecebedor);
      eventDispatcher.dispatch(transferencia.domainEvents());
      transferencia.clearEvents();
    } catch (Exception e) {
      transferencia.clearEvents();
      throw e;
    }
  }
}
