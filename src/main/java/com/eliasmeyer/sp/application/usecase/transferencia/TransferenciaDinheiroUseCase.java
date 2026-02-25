package com.eliasmeyer.sp.application.usecase.transferencia;

import com.eliasmeyer.sp.application.port.transferencia.in.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.application.port.transferencia.in.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.application.port.transferencia.out.AutorizacaoOutputPort;
import com.eliasmeyer.sp.application.port.user.out.UsuarioOutputPort;
import com.eliasmeyer.sp.application.shared.DefaultDomainEventDispatcher;
import com.eliasmeyer.sp.domain.model.carteira.Dinheiro;
import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;

public class TransferenciaDinheiroUseCase implements EfetuarTransferenciaInputPort {

    private final AutorizacaoOutputPort autorizacaoOutputPort;

    private final UsuarioOutputPort usuarioOutputPort;

    private final DefaultDomainEventDispatcher eventDispatcher;

    public TransferenciaDinheiroUseCase(AutorizacaoOutputPort autorizacaoOutputPort, UsuarioOutputPort usuarioOutputPort, DefaultDomainEventDispatcher eventDispatcher) {
        this.autorizacaoOutputPort = autorizacaoOutputPort;
        this.usuarioOutputPort = usuarioOutputPort;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void execute(EfetuarTransferenciaCommand command) {
        UsuarioId idPagador = new UsuarioId(command.IdRecebedor());
        UsuarioId idRecebedor = new UsuarioId(command.IdRecebedor());
        Dinheiro quantia = new Dinheiro(command.quantia());

        Usuario uPagador = usuarioOutputPort.buscarPorId(idPagador).orElseThrow(() -> new IllegalArgumentException(String.format("Usuário não encontrado com o id [%s]", idPagador)));
        Usuario uRecebedor = usuarioOutputPort.buscarPorId(idRecebedor).orElseThrow(() -> new IllegalArgumentException(String.format("Usuário não encontrado com o id [%s]", idRecebedor)));

        Transferencia transferencia = new Transferencia(uPagador, uRecebedor, quantia);
        transferencia.reservar();

        if (!autorizacaoOutputPort.isAutorizado(idPagador)) {
            transferencia.falhar();
            throw new TransferenciaNaoAutorizadaException(String.format("Usuário [%s] não autorizado para transferir dinheiro.", idPagador));
        }

        transferencia.realizar();
        eventDispatcher.dispatch(transferencia.domainEvents());
        transferencia.clearEvents();
    }
}
