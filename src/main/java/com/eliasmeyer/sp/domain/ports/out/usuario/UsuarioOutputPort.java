package com.eliasmeyer.sp.domain.ports.out.usuario;

import com.eliasmeyer.sp.domain.model.usuario.Documento;
import com.eliasmeyer.sp.domain.model.usuario.Email;
import com.eliasmeyer.sp.domain.model.usuario.Usuario;
import com.eliasmeyer.sp.domain.model.usuario.UsuarioId;
import java.util.Optional;

public interface UsuarioOutputPort {

	void salvar(Usuario usuario);

	Optional<Usuario> buscarPorId(UsuarioId id);

	Optional<Usuario> buscarPorDocumento(Documento documento);

	Optional<Usuario> buscarPorEmail(Email email);
}
