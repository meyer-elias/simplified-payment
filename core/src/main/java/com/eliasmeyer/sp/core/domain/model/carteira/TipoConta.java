package com.eliasmeyer.sp.core.domain.model.carteira;

public enum TipoConta {

	COMUM((short) 1),
	LOJISTA((short) 2);

	private final Short codigo;

	TipoConta(Short codigo) {
		this.codigo = codigo;
	}

	public Short getCodigo() {
		return codigo;
	}
}
