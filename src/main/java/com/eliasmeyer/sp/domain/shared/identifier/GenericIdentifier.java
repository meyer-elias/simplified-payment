package com.eliasmeyer.sp.domain.shared.identifier;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class GenericIdentifier<T extends Serializable & Comparable<T>> implements Identifier<T> {

    private final T value;

    protected GenericIdentifier(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    protected T validate(T value) {
        Objects.requireNonNull(value, "Valor do identificador não pode ser nulo");
        return value;
    }

    protected void addValidation(Predicate<T> validation, String errorMessage) {
        if (!validation.test(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GenericIdentifier<?> other = (GenericIdentifier<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("Identifier[%s:%s]",
                value.getClass().getSimpleName(), value);
    }
}
