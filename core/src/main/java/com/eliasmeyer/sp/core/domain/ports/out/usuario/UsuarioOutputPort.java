package com.eliasmeyer.sp.core.domain.ports.out.usuario;

import com.eliasmeyer.sp.core.domain.model.usuario.Documento;
import com.eliasmeyer.sp.core.domain.model.usuario.Email;
import com.eliasmeyer.sp.core.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.core.domain.model.usuario.UsuarioId;
import java.util.Optional;

public interface UsuarioOutputPort {

	void salvar(Usuario usuario);

	Optional<Usuario> buscarPorId(UsuarioId id);

	Optional<Usuario> buscarPorDocumento(Documento documento);

	Optional<Usuario> buscarPorEmail(Email email);
}
