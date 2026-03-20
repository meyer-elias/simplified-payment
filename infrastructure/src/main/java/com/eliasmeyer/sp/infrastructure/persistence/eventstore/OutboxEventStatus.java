package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

public enum OutboxEventStatus {
	PENDENTE((short) 1),
	PUBLICADO((short) 2),
	FALHADO((short) 3);

	private final Short codigo;

	OutboxEventStatus(Short codigo) {
		this.codigo = codigo;
	}

	public Short getCodigo() {
		return codigo;
	}
}
