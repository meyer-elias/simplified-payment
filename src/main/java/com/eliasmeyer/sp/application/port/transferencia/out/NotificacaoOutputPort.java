package com.eliasmeyer.sp.application.port.transferencia.out;

import com.eliasmeyer.sp.domain.model.transferencia.TransferenciaId;

public interface NotificacaoOutputPort {

    void notificar(TransferenciaId transferenciaId);
}
