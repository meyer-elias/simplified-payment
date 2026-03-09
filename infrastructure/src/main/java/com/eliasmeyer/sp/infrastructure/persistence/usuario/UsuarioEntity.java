package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import com.eliasmeyer.sp.core.domain.model.usuario.TipoUsuario;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_USUARIO")
public class UsuarioEntity {

	@Id
	@Column(name = "COD_USUARIO", nullable = false, updatable = false)
	private String id;

	@Column(name = "DOCUMENTO", nullable = false, unique = true)
	private String documento;

	@Column(name = "NOME", nullable = false)
	private String nome;

	@Column(name = "EMAIL", nullable = false, unique = true)
	private String email;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "TP_USUARIO", nullable = false)
	@Convert(converter = TipoUsuarioConverter.class)
	private TipoUsuario tipoUsuario;
}
