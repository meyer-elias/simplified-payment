package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
class UsuarioJpaAdapter implements PanacheRepositoryBase<UsuarioEntity, String> {

	public Optional<UsuarioEntity> findByEmail(String email) {
		return find("email", email).firstResultOptional();
	}

	public Optional<UsuarioEntity> findByDocumento(String documento) {
		return find("documento", documento).firstResultOptional();
	}

}
