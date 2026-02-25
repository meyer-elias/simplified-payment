package com.eliasmeyer.sp.domain.shared;


import com.eliasmeyer.sp.domain.shared.identifier.Identifier;
import java.io.Serializable;
import java.util.Objects;

/**
 * Classe base genérica para todas as Entidades no domínio.
 *
 * @param <T> Tipo do identificador (deve implementar Identifier)
 */
public abstract class Entity<T extends Identifier<?>> {

  private final T t;

  protected Entity(T id) {
    Objects.requireNonNull(id, "Id não pode ser nulo");
    this.t = id;
  }

  public T getId() {
    return t;
  }

  public <R extends Serializable & Comparable<R>> R getIdValue() {
    return (R) t.getValue();
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
    return Objects.equals(t, other.t);
  }

  @Override
  public int hashCode() {
    return Objects.hash(t);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + t + "]";
  }
}