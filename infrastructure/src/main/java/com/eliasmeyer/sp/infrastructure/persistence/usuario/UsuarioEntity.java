package com.eliasmeyer.sp.infrastructure.persistence.usuario;

import com.eliasmeyer.sp.core.domain.model.usuario.TipoUsuario;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "TB_USUARIO", indexes = {
	@Index(name = "IDX_USUARIO_EMAIL", columnList = "EMAIL"),
	@Index(name = "IDX_USUARIO_DOCUMENTO", columnList = "DOCUMENTO")
}, uniqueConstraints = {
	@UniqueConstraint(name = "UN_USUARIO_EMAIL", columnNames = "EMAIL"),
	@UniqueConstraint(name = "UN_USUARIO_DOCUMENTO", columnNames = "DOCUMENTO"),
})
public class UsuarioEntity extends PanacheEntityBase {

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

	@Column(name = "TP_USUARIO", nullable = false, columnDefinition = "SMALLINT")
	@Convert(converter = TipoUsuarioConverter.class)
	private TipoUsuario tipoUsuario;

	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
}
