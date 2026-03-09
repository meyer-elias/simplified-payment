package com.eliasmeyer.sp.core.domain.shared;


import com.eliasmeyer.sp.core.domain.shared.identifier.Identifier;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe base genérica para todas as Entidades no domínio.
 *
 * @param <I> Tipo do identificador (deve implementar Identifier)
 */
public abstract class Entity<I extends Identifier<?>> {

	protected final I id;

	protected Entity(I id) {
		Objects.requireNonNull(id, "Id não pode ser nulo");
		this.id = id;
	}

	public I getId() {
		return id;
	}

	public <R extends Serializable & Comparable<R>> R getIdValue() {
		return (R) id.getValue();
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
		return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id=" + id + "]";
	}
}