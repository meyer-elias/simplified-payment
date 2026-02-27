package com.eliasmeyer.sp.domain.ports.out.transferencia;

import com.eliasmeyer.sp.domain.shared.identifier.Id;

public interface TransferenciaAutorizadorOutputPort {

	boolean isAutorizado(Id idUsuario);
}
