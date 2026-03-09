package com.eliasmeyer.sp.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record EfetuarTransferenciaRequest(

	@NotBlank(message = "Identificação da conta do pagador é obrigatório")
	String idPagador,
	@NotBlank(message = "Identificação da conta do recebedor é obrigatório")
	String idRecebedor,
	@NotNull(message = "Quantia da transferência é obrigatória.")
	@Positive(message = "Quantia deve ser um valor positivo.")
	BigDecimal quantia) {

}
