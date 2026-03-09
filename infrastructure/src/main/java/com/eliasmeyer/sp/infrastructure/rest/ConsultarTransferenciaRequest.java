package com.eliasmeyer.sp.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ConsultarTransferenciaRequest(

	@NotBlank(message = "Identificação da conta é obrigatória")
	String carteiraId,

	@PositiveOrZero(message = "Página inicial deve ser positiva ou zero.")
	int pageInicia,

	@Positive(message = "Tamanho da página deve ser positiva.")
	int tamanho) {

}
