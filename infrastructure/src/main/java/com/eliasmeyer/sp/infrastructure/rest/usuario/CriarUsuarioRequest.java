package com.eliasmeyer.sp.infrastructure.rest.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CriarUsuarioRequest(
	@NotBlank(message = "Nome é obrigatório")
	String nome,
	@NotBlank(message = "Documento (CPF ou CNPJ) é obrigatório")
	String documento,
	@NotBlank(message = "E-mail é obrigatório")
	String email,
	@NotBlank(message = "Senha é obrigatória")
	String senha,
	@NotNull(message = "Valor inicial é obrigatório")
	@Positive(message = "Valor inicial deve ser positivo")
	BigDecimal valorInicial) {

}
