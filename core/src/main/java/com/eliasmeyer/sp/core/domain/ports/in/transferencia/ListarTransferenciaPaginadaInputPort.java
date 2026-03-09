package com.eliasmeyer.sp.core.domain.ports.in.transferencia;

import java.util.List;

public interface ListarTransferenciaPaginadaInputPort {

	List<TransferenciaOutput> execute(ListarTransferenciaPaginadaCommand command);

}
