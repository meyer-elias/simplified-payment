package com.eliasmeyer.sp.core.domain.model.carteira;

public enum TipoConta {

	COMUM(1),
	LOJISTA(2);

	private final int codigo;

	TipoConta(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}
}
