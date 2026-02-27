package com.eliasmeyer.sp.domain.ports.out.transferencia;

import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;

public interface TransferenciaOutputPort {

	void salvar(Transferencia transferencia);
}
