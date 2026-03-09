package com.eliasmeyer.sp.core.domain.ports.in.transferencia;

public record ListarTransferenciaPaginadaCommand(
	String contaId,
	int paginaInicial,
	int tamanhoPagina) {

}
