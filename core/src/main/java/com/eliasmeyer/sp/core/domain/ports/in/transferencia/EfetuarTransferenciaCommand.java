package com.eliasmeyer.sp.core.domain.ports.in.transferencia;

import java.math.BigDecimal;

public record EfetuarTransferenciaCommand(
	String idempotencyKey,
	String idPagador,
	String idRecebedor,
	BigDecimal quantia) {

}
