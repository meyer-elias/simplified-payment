package com.eliasmeyer.sp.core.domain.model.usuario;

import java.util.Objects;

/**
 * Classe abstrata que representa um documento de identificação. Subclasses concretas (CPF, CNPJ)
 * implementam validações específicas.
 */
public abstract class Documento {

	protected final String numero;

	protected Documento(String numero) {
		this.numero = Objects.requireNonNull(numero, "Número de documento não pode ser nulo");
	}

	/**
	 * Retorna o número do documento formatado.
	 */
	public abstract String getNumero();

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Documento)) {
			return false;
		}
		Documento documento = (Documento) o;
		return numero.equals(documento.numero);
	}

	@Override
	public int hashCode() {
		return Objects.hash(numero);
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", this.getClass().getSimpleName(), numero);
	}
}
