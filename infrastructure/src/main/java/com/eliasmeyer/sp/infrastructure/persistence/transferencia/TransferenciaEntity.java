package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.transferencia.TransferenciaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_TRANSFERENCIA")
class TransferenciaEntity {

	@Id
	@Column(name = "COD_TRANSFERENCIA", nullable = false, updatable = false)
	private String id;

	@Column(name = "COD_CARTEIRA_PAG", nullable = false)
	private String carteiraIdPagador;

	@Column(name = "COD_CARTEIRA_REC", nullable = false)
	private String carteiraIdRecebedor;

	@Column(name = "VLR_TRANSACAO", scale = 4, precision = 19, nullable = false)
	private BigDecimal valor;

	@Column(name = "TS_CRIADO_EM", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@Column(name = "TS_ATUALIZADO_EM", nullable = false)
	private LocalDateTime atualizadoEm;

	@Column(name = "STATUS", nullable = false)
	@Convert(converter = TransferenciaStatusConverter.class)
	private TransferenciaStatus status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarteiraIdPagador() {
		return carteiraIdPagador;
	}

	public void setCarteiraIdPagador(String carteiraIdPagador) {
		this.carteiraIdPagador = carteiraIdPagador;
	}

	public String getCarteiraIdRecebedor() {
		return carteiraIdRecebedor;
	}

	public void setCarteiraIdRecebedor(String carteiraIdRecebedor) {
		this.carteiraIdRecebedor = carteiraIdRecebedor;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDateTime getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(LocalDateTime criadoEm) {
		this.criadoEm = criadoEm;
	}

	public LocalDateTime getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(LocalDateTime atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	public TransferenciaStatus getStatus() {
		return status;
	}

	public void setStatus(TransferenciaStatus status) {
		this.status = status;
	}
}
