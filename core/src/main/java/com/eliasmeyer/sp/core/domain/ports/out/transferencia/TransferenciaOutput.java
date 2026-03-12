package com.eliasmeyer.sp.core.domain.ports.out.transferencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaOutput(
	LocalDateTime dataHora,
	BigDecimal valor,
	String contaId) {

}
