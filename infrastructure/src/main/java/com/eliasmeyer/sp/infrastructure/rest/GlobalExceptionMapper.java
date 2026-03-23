package com.eliasmeyer.sp.infrastructure.rest;

import static jakarta.ws.rs.core.Response.status;

import com.eliasmeyer.sp.core.application.exception.TransferenciaIndisponivelException;
import com.eliasmeyer.sp.core.application.exception.TransferenciaNaoAutorizadaException;
import com.eliasmeyer.sp.core.application.shared.ApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		return switch (exception) {
			case TransferenciaNaoAutorizadaException e ->
				errorResponse(Status.UNAUTHORIZED, "TRANSFERENCIA_NAO_AUTORIZADA",
					e);
			case TransferenciaIndisponivelException e ->
				errorResponse(Status.SERVICE_UNAVAILABLE, "SERVICO_INDISPONIVEL", e);
			case IllegalArgumentException e ->
				errorResponse(Status.BAD_REQUEST, "REQUISICAO_INVALIDA", e);
			case ApplicationException e ->
				errorResponse(Status.INTERNAL_SERVER_ERROR, "ERRO_INTERNO", e);
			default -> errorResponse(Status.INTERNAL_SERVER_ERROR, "ERRO_INESPERADO", exception);
		};
	}

	private Response errorResponse(Status status, String code, Exception e) {
		return status(status)
			.entity(new ErrorResponse(code, e.getMessage(), UUID.randomUUID().toString()))
			.build();
	}


	public record ErrorResponse(String code, String message, String traceId) {

	}
}