package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

public enum OutboxEventStatus {
	PENDENTE(1),
	PUBLICADO(2),
	FALHADO(3);

	private final int codigo;

	OutboxEventStatus(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}
}
