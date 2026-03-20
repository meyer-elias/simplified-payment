package com.eliasmeyer.sp.core.domain.model.transferencia;

public enum TransferenciaStatus {

	CRIADA((short) 1),
	RESERVADA((short) 2),
	REALIZADA((short) 3),
	CANCELADA((short) 4),
	FALHADA((short) 5);

	private final Short codigo;

	TransferenciaStatus(Short codigo) {
		this.codigo = codigo;
	}

	public Short getCodigo() {
		return codigo;
	}

}
