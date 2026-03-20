package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import com.eliasmeyer.sp.core.domain.model.carteira.TipoConta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Objects;

@Converter(autoApply = true)
class TipoContaConverter implements AttributeConverter<TipoConta, Short> {

	@Override
	public TipoConta convertToEntityAttribute(Short codigo) {
		return Objects.isNull(codigo) ? null
			: Arrays.stream(TipoConta.values()).filter(tp -> Objects.equals(tp.getCodigo(), codigo))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
					String.format("Código [%d] TipoConta não encontrado", codigo)));
	}

	@Override
	public Short convertToDatabaseColumn(TipoConta tipoConta) {
		return Objects.isNull(tipoConta) ? null : tipoConta.getCodigo();
	}
}
