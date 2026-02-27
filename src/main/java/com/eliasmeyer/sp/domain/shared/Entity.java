package com.eliasmeyer.sp.domain.shared;


import com.eliasmeyer.sp.domain.shared.identifier.Identifier;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe base genérica para todas as Entidades no domínio.
 *
 * @param <ID> Tipo do identificador (deve implementar Identifier)
 */
public abstract class Entity<ID extends Identifier<?>> {

	protected final ID ID;

	protected Entity(ID id) {
		Objects.requireNonNull(id, "Id não pode ser nulo");
		this.ID = id;
	}

	public ID getId() {
		return ID;
	}

	public <R extends Serializable & Comparable<R>> R getIdValue() {
		return (R) ID.getValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		Entity<?> other = (Entity<?>) obj;
		return Objects.equals(ID, other.ID);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ID);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id=" + ID + "]";
	}
}