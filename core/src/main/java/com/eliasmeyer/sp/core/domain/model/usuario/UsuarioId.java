package com.eliasmeyer.sp.core.domain.model.usuario;

import com.eliasmeyer.sp.core.domain.shared.identifier.Id;
import java.util.UUID;

public class UsuarioId extends Id {

	public UsuarioId() {
	}

	public UsuarioId(UUID uuid) {
		super(uuid);
	}

	public UsuarioId(String uuidString) {
		super(uuidString);
	}
}
