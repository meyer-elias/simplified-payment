package com.eliasmeyer.sp.infrastructure.persistence.eventstore;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Objects;

@Converter(autoApply = true)
class OutboxEventStatusConverter implements AttributeConverter<OutboxEventStatus, Short> {

	@Override
	public OutboxEventStatus convertToEntityAttribute(Short codigo) {
		if (Objects.isNull(codigo)) {
			return null;
		}

		return Arrays.stream(OutboxEventStatus.values())
			.filter(status -> Objects.equals(status.getCodigo(), codigo))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(
				String.format("Enum %s não existe com o código %d",
					OutboxEventStatus.class.getSimpleName(), codigo)));
	}

	@Override
	public Short convertToDatabaseColumn(OutboxEventStatus outboxEventStatus) {
		return Objects.isNull(outboxEventStatus) ? null : outboxEventStatus.getCodigo();
	}
}
