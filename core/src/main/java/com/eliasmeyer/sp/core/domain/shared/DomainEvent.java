package com.eliasmeyer.sp.core.domain.shared;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

	/**
	 * Identificador único do evento.
	 */
	UUID eventId();

	/**
	 * Tipo do agregado que originou o evento (ex: "Transferencia", "Conta").
	 */
	String aggregateType();

	/**
	 * Identificador do agregado que originou o evento.
	 */
	UUID aggregateId();

	/**
	 * Momento em que o evento ocorreu.
	 */
	Instant occurredOn();

}
