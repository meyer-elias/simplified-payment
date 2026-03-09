package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import com.eliasmeyer.sp.core.domain.model.carteira.TipoConta;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CARTEIRA")
public class CarteiraEntity {

	@Id
	@Column(name = "COD_CARTEIRA", nullable = false, updatable = false)
	private String id;

	@JoinColumn(name = "COD_USUARIO", nullable = false, unique = true)
	private String usuarioId;

	@Column(name = "VLR_SALDO", nullable = false, scale = 4, precision = 19)
	private BigDecimal saldoDisponivel;

	@Column(name = "VLR_SALDO_RESERVADO", nullable = false, scale = 4, precision = 19)
	private BigDecimal saldoReservado;

	@Column(name = "TIPO_CONTA", nullable = false)
	@Convert(converter = TipoContaConverter.class)
	private TipoConta tipoConta;

	@Column(name = "TS_ULTIMA_ATUALIZACAO", nullable = false)
	private LocalDateTime ultimaAtualizacao;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public BigDecimal getSaldoDisponivel() {
		return saldoDisponivel;
	}

	public void setSaldoDisponivel(BigDecimal saldoDisponivel) {
		this.saldoDisponivel = saldoDisponivel;
	}

	public BigDecimal getSaldoReservado() {
		return saldoReservado;
	}

	public void setSaldoReservado(BigDecimal saldoReservado) {
		this.saldoReservado = saldoReservado;
	}

	public TipoConta getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(TipoConta tipoConta) {
		this.tipoConta = tipoConta;
	}

	public LocalDateTime getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}
}

