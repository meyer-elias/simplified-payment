package com.eliasmeyer.sp.infrastructure.rest.usuario;

import com.eliasmeyer.sp.core.domain.ports.in.usuario.CriarUsuarioCommand;
import com.eliasmeyer.sp.core.domain.ports.in.usuario.CriarUsuarioInputPort;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

@Tag(name = "Usuários", description = "Operações relacionadas a usuários")
@Path("usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

	private final CriarUsuarioInputPort criarUsuarioInputPort;

	@Inject
	public UsuarioResource(CriarUsuarioInputPort criarUsuarioInputPort) {
		this.criarUsuarioInputPort = criarUsuarioInputPort;
	}

	@POST
	@APIResponse(responseCode = "201", description = "Usuário criado com sucesso")
	@APIResponse(responseCode = "400", description = "Dados inválidos")
	public RestResponse<Void> criarUsuario(@Valid CriarUsuarioRequest request) {
		criarUsuarioInputPort.execute(new CriarUsuarioCommand(
			request.documento(), request.nome(), request.email(),
			request.senha(), request.valorInicial()));
		return RestResponse.status(Response.Status.CREATED);
	}
}