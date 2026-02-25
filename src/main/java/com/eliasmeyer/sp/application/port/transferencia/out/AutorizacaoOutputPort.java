package com.eliasmeyer.sp.application.port.transferencia.out;

import com.eliasmeyer.sp.domain.shared.identifier.Id;

public interface AutorizacaoOutputPort {

    boolean isAutorizado(Id idUsuario);
}
