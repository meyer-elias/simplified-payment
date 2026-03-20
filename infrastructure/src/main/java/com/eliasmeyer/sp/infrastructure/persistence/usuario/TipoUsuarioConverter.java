package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import com.eliasmeyer.sp.core.domain.model.usuario.TipoUsuario;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.Objects;

@Converter(autoApply = true)
class TipoUsuarioConverter implements AttributeConverter<TipoUsuario, Short> {

	@Override
	public Short convertToDatabaseColumn(TipoUsuario tipoUsuario) {
		if (Objects.isNull(tipoUsuario)) {
			return null;
		}
		return tipoUsuario.getCodigo();
	}

	@Override
	public TipoUsuario convertToEntityAttribute(Short codigo) {
		if (Objects.isNull(codigo)) {
			return null;
		}
		return Arrays.stream(TipoUsuario.values())
			.filter(tipoUsuario -> Objects.equals(tipoUsuario.getCodigo(), codigo))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(
				String.format("Enum TipoUsuario não existe com o código %d", codigo)));
	}
}
