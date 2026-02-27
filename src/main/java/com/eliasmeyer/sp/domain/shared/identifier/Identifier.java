package com.eliasmeyer.sp.domain.shared.identifier;

import java.io.Serializable;

public interface Identifier<T extends Serializable & Comparable<T>> extends Serializable,
	Comparable<Identifier<T>> {

	T getValue();

	default String asString() {
		return getValue().toString();
	}

	@Override
	default int compareTo(Identifier<T> other) {
		return this.getValue().compareTo(other.getValue());
	}
}
