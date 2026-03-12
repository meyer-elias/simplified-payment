package com.eliasmeyer.sp.infrastructure.ports.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v1/authorizer")
@RegisterRestClient(configKey = "autorizador-api")
public interface AutorizadorTransferenciaClient {

	@GET
	@Path("/users/{idUsuario}")
	@Produces(MediaType.APPLICATION_JSON)
	AutorizacaoTransferenciaResponse verificar(@PathParam("idUsuario") String idUsuario);

}
