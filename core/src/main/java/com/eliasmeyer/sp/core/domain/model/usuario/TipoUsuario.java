package com.eliasmeyer.sp.core.domain.model.usuario;

public enum TipoUsuario {

	COMUM((short) 1, "Comum"),
	LOJISTA((short) 2, "Lojista");

	private final Short codigo;

	private final String descricao;

	TipoUsuario(Short codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Short getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
