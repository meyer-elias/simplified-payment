package com.eliasmeyer.sp.core.domain.ports.in.transferencia;

import java.util.List;

public interface ListarTransferenciaInputPort {

	List<TransferenciaOutput> execute(ListarTransferenciaPaginadaCommand command);

}
