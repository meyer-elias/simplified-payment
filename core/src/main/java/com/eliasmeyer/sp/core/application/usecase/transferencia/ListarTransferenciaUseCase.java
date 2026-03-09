package com.eliasmeyer.sp.core.application.usecase.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaPaginadaCommand;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaPaginadaInputPort;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.TransferenciaOutput;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import java.util.List;

public class ListarTransferenciaUseCase implements ListarTransferenciaPaginadaInputPort {

	private final TransferenciaOutputPort transferenciaOutputPort;

	public ListarTransferenciaUseCase(TransferenciaOutputPort transferenciaOutputPort) {
		this.transferenciaOutputPort = transferenciaOutputPort;
	}

	private static TransferenciaOutput mapToOutput(Transferencia t) {
		return new TransferenciaOutput(t.getCriadoEm(), t.getQuantia().getValor(),
			t.getPagador().getId().asString());
	}

	@Override
	public List<TransferenciaOutput> execute(ListarTransferenciaPaginadaCommand command) {
		CarteiraId carteiraId = new CarteiraId(command.contaId());
		return transferenciaOutputPort.buscarPorCarteiraIdPaginada(
				carteiraId,
				command.paginaInicial(), command.tamanhoPagina())
			.stream()
			.map(ListarTransferenciaUseCase::mapToOutput)
			.toList();
	}
}
