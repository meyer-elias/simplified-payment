package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import com.eliasmeyer.sp.core.domain.model.usuario.DocumentoFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Nome;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioFactory;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class UsuarioMapper {

	public Usuario toDomain(UsuarioEntity entity) {
		return UsuarioFactory.criar(
			new UsuarioId(entity.getId()),
			DocumentoFactory.criar(entity.getDocumento()),
			new Nome(entity.getNome()),
			new Email(entity.getEmail()),
			entity.getTipoUsuario());
	}

	public UsuarioEntity toEntity(Usuario usuario) {
		UsuarioEntity entity = new UsuarioEntity();
		entity.setId(usuario.getId().asString());
		entity.setDocumento(usuario.getDocumento().getNumero());
		entity.setEmail(usuario.getEmail().address());
		entity.setNome(usuario.getNome().nome());
		entity.setPassword(usuario.getSenha());
		entity.setTipoUsuario(usuario.getTipo());
		return entity;
	}
}