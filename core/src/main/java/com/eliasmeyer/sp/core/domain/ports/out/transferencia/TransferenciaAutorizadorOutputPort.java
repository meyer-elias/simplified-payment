package com.eliasmeyer.sp.core.domain.ports.out.transferencia;

import com.eliasmeyer.sp.core.domain.shared.identifier.Id;

public interface TransferenciaAutorizadorOutputPort {

	boolean isAutorizado(Id idUsuario);
}
