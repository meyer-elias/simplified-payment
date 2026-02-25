package com.eliasmeyer.sp.domain.model.usuario;

import com.eliasmeyer.sp.domain.shared.identifier.Id;

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
