package com.eliasmeyer.sp.core.domain.model.transferencia;

public enum TransferenciaStatus {

	CRIADA(1),
	RESERVADA(2),
	REALIZADA(3),
	CANCELADA(4),
	FALHADA(5);

	private final int codigo;

	TransferenciaStatus(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}

}
