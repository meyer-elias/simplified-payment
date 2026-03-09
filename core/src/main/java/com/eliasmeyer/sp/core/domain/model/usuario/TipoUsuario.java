package com.eliasmeyer.sp.core.domain.model.usuario;

public enum TipoUsuario {

	COMUM(1, "Comum"),
	LOJISTA(2, "Lojista");

	private final int codigo;

	private final String descricao;

	TipoUsuario(int codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public int getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
