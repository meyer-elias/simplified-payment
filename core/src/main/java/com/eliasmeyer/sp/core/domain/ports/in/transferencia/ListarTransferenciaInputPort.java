package com.eliasmeyer.sp.core.domain.ports.in.transferencia;

import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutput;
import java.util.List;

public interface ListarTransferenciaInputPort {

	List<TransferenciaOutput> execute(ListarTransferenciaPaginadaCommand command);

}
