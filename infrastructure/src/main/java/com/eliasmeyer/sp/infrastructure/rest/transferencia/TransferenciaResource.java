package com.eliasmeyer.sp.infrastructure.rest.transferencia;

import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaCommand;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.EfetuarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaInputPort;
import com.eliasmeyer.sp.core.domain.ports.in.transferencia.ListarTransferenciaPaginadaCommand;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutput;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

@Tag(name = "Transferências", description = "Operações relacionadas a transferências financeiras")
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


	@Operation(summary = "Efetuar transferência entre contas",
		description = "Realiza uma transferência de valores entre duas carteiras")
	@APIResponse(responseCode = "200", description = "Transferência realizada com sucesso")
	@APIResponse(responseCode = "400", description = "Dados inválidos na requisição")
	@APIResponse(responseCode = "500", description = "Erro interno do servidor")
	@POST
	public RestResponse<Void> efetuarTransferencia(
		@NotNull @HeaderParam("idempotencyKey") String idempotencyKey,
		@Valid @RequestBody(description = "Dados da transferência") EfetuarTransferenciaRequest request) {
		EfetuarTransferenciaCommand command = new EfetuarTransferenciaCommand(
			idempotencyKey,
			request.idPagador(),
			request.idRecebedor(),
			request.quantia());
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
