package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import com.eliasmeyer.sp.core.domain.model.carteira.TipoConta;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Objects;

@Converter(autoApply = true)
class TipoContaConverter implements AttributeConverter<TipoConta, Integer> {

	@Override
	public TipoConta convertToEntityAttribute(Integer codigo) {
		return Objects.isNull(codigo) ? null
			: Arrays.stream(TipoConta.values()).filter(tp -> tp.getCodigo() == codigo).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(
					String.format("Código [%d] TipoConta não encontrado", codigo)));
	}

	@Override
	public Integer convertToDatabaseColumn(TipoConta tipoConta) {
		return Objects.isNull(tipoConta) ? null : tipoConta.getCodigo();
	}
}
