package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import com.eliasmeyer.sp.core.domain.model.usuario.Documento;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import com.eliasmeyer.sp.core.domain.ports.out.usuario.UsuarioOutputPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements UsuarioOutputPort {

	private final UsuarioJpaAdapter usuarioJpaAdapter;

	private final UsuarioMapper usuarioMapper;

	@Inject
	public UsuarioRepository(UsuarioJpaAdapter usuarioJpaAdapter, UsuarioMapper usuarioMapper) {
		this.usuarioJpaAdapter = usuarioJpaAdapter;
		this.usuarioMapper = usuarioMapper;
	}

	@Override
	public Optional<Usuario> buscarPorEmail(Email email) {
		return usuarioJpaAdapter.findByEmail(email.address())
			.map(usuarioMapper::toDomain);
	}

	@Override
	public Optional<Usuario> buscarPorDocumento(Documento documento) {
		return usuarioJpaAdapter.findByDocumento(documento.getNumero())
			.map(usuarioMapper::toDomain);
	}

	@Override
	public Optional<Usuario> buscarPorId(UsuarioId id) {
		return usuarioJpaAdapter.findByIdOptional(id.asString())
			.map(usuarioMapper::toDomain);
	}

	@Override
	public void salvar(Usuario usuario) {
		usuarioJpaAdapter.persist(usuarioMapper.toEntity(usuario));
	}

	@Transactional
	void deleteAll() {
		usuarioJpaAdapter.deleteAll();
	}
}
