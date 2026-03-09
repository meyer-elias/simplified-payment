package com.eliasmeyer.sp.infrastructure.rest;

import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaPaginadaCommand;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.TransferenciaOutput;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.jboss.resteasy.reactive.RestResponse;

@Path("transferencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferenciaResource {

	private final EfetuarTransferenciaInputPort efetuarTransferenciaInputPort;

	private final ListarTransferenciaInputPort listarTransferenciaInputPort;

	@Inject
	public TransferenciaResource(EfetuarTransferenciaInputPort efetuarTransferenciaInputPort,
		ListarTransferenciaInputPort listarTransferenciaInputPort) {
		this.efetuarTransferenciaInputPort = efetuarTransferenciaInputPort;
		this.listarTransferenciaInputPort = listarTransferenciaInputPort;
	}

	@POST
	public RestResponse<Void> efetuarTransferencia(@Valid EfetuarTransferenciaRequest request) {
		EfetuarTransferenciaCommand command = new EfetuarTransferenciaCommand(request.idPagador(),
			request.idRecebedor(), request.quantia());
		efetuarTransferenciaInputPort.execute(command);
		return RestResponse.ok();
	}

	@GET
	public RestResponse<List<TransferenciaOutput>> listarTransferencia(
		@Valid ConsultarTransferenciaRequest request) {
		ListarTransferenciaPaginadaCommand command = new ListarTransferenciaPaginadaCommand(
			request.carteiraId(), request.pageInicia(), request.tamanho());
		return RestResponse.ok(listarTransferenciaInputPort.execute(command));
	}
}
