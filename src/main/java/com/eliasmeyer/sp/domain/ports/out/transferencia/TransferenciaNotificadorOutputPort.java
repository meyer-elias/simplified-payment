package com.eliasmeyer.sp.domain.ports.out.transferencia;

import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;

public interface TransferenciaNotificadorOutputPort {

  void notificar(Transferencia transferencia);
}
