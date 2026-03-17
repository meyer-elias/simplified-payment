package com.eliasmeyer.sp.core.domain.model.transferencia.eventos;

import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.shared.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferenciaCanceladaEvento(
	UUID eventId,
	UUID transferenciaId,
	UUID contaOrigemId,
	UUID contaDestinoId,
	BigDecimal valor,
	Instant occurredOn
) implements DomainEvent {

	public TransferenciaCanceladaEvento(Transferencia transferencia) {
		this(
			UUID.randomUUID(),
			transferencia.getId().getValue(),
			transferencia.getPagador().getId().getValue(),
			transferencia.getRecebedor().getId().getValue(),
			transferencia.getQuantia().getValor(),
			Instant.now()
		);
	}

	@Override
	public String aggregateType() {
		return Transferencia.class.getSimpleName();
	}

	@Override
	public UUID aggregateId() {
		return transferenciaId;
	}
}
