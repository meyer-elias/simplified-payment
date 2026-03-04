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

	protected final I i;

	protected Entity(I i) {
		Objects.requireNonNull(i, "Id não pode ser nulo");
		this.i = i;
	}

	public I getId() {
		return i;
	}

	public <R extends Serializable & Comparable<R>> R getIdValue() {
		return (R) i.getValue();
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
		return Objects.equals(i, other.i);
	}

	@Override
	public int hashCode() {
		return Objects.hash(i);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id=" + i + "]";
	}
}