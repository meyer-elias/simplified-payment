package com.eliasmeyer.sp.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.jboss.resteasy.reactive.RestQuery;

public record ConsultarTransferenciaRequest(

	@RestQuery
	@NotBlank(message = "Identificação da conta é obrigatória")
	String carteiraId,

	@RestQuery
	@PositiveOrZero(message = "Página inicial deve ser positiva ou zero.")
	int pageInicia,

	@RestQuery
	@Positive(message = "Tamanho da página deve ser positiva.")
	int tamanho) {

}
