package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.transferencia.TransferenciaStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Objects;

@Converter(autoApply = true)
class TransferenciaStatusConverter implements
	AttributeConverter<TransferenciaStatus, Integer> {

	@Override
	public TransferenciaStatus convertToEntityAttribute(Integer codigo) {
		return Objects.isNull(codigo) ? null : Arrays.stream(TransferenciaStatus.values())
			.filter(t -> t.getCodigo() == codigo)
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Código Status não encontrado."));

	}

	@Override
	public Integer convertToDatabaseColumn(TransferenciaStatus transferenciaStatus) {
		return Objects.isNull(transferenciaStatus) ? null : transferenciaStatus.getCodigo();
	}
}
