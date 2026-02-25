package com.eliasmeyer.sp.application.port.transferencia.out;

import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;

public interface TransferenciaOutputPort {

    void adicionar(Transferencia transferencia);
}
