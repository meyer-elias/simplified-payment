package com.eliasmeyer.sp.infrastructure.messaging;

import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record NotificacaoTransferenciaRealizadaPayload(
	UUID transferenciaId,
	UUID pagadorId,
	UUID recebedorId,
	BigDecimal valor,
	Instant realizadaEm) {

	static NotificacaoTransferenciaRealizadaPayload from(Transferencia transferencia) {
		return new NotificacaoTransferenciaRealizadaPayload(
			transferencia.getId().getValue(),
			transferencia.getPagador().getUsuarioId().getValue(),
			transferencia.getRecebedor().getUsuarioId().getValue(),
			transferencia.getQuantia().getValor(),
			Instant.now());
	}
}
