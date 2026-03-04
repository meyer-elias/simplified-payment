package com.eliasmeyer.sp.core.domain.ports.out.transferencia;

import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;

public interface TransferenciaOutputPort {

	void salvar(Transferencia transferencia);
}
