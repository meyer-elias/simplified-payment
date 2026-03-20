package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.transferencia.TransferenciaStatus;
import com.eliasmeyer.sp.infrastructure.persistence.carteira.CarteiraEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_TRANSFERENCIA", indexes = {
	@Index(name = "IDX_TRANSFERENCIA_COD_CARTEIRA_PAG", columnList = "COD_CARTEIRA_PAG"),
	@Index(name = "IDX_TRANSFERENCIA_COD_CARTEIRA_REC", columnList = "COD_CARTEIRA_REC")
})
class TransferenciaEntity extends PanacheEntityBase {

	@Id
	@Column(name = "COD_TRANSFERENCIA", nullable = false, updatable = false)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COD_CARTEIRA_PAG", nullable = false, foreignKey = @ForeignKey(name = "FK_TRANSFERENCIA_CARTEIRA_PAG",
		foreignKeyDefinition = "FOREIGN KEY (COD_CARTEIRA_PAG) REFERENCES TB_CARTEIRA(COD_CARTEIRA)"))
	private CarteiraEntity carteiraPagador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COD_CARTEIRA_REC", nullable = false, foreignKey = @ForeignKey(name = "FK_TRANSFERENCIA_CARTEIRA_REC",
		foreignKeyDefinition = "FOREIGN KEY (COD_CARTEIRA_REC) REFERENCES TB_CARTEIRA(COD_CARTEIRA)"))
	private CarteiraEntity carteiraRecebedor;

	@Column(name = "VLR_TRANSACAO", scale = 4, precision = 19, nullable = false)
	private BigDecimal valor;

	@Column(name = "TS_CRIADO_EM", nullable = false, updatable = false)
	private LocalDateTime criadoEm;

	@Column(name = "TS_ATUALIZADO_EM", nullable = false)
	private LocalDateTime atualizadoEm;

	@Column(name = "STATUS", nullable = false, columnDefinition = "SMALLINT")
	private TransferenciaStatus status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CarteiraEntity getCarteiraPagador() {
		return carteiraPagador;
	}

	public void setCarteiraPagador(CarteiraEntity carteiraPagador) {
		this.carteiraPagador = carteiraPagador;
	}

	public CarteiraEntity getCarteiraRecebedor() {
		return carteiraRecebedor;
	}

	public void setCarteiraRecebedor(CarteiraEntity carteiraRecebedor) {
		this.carteiraRecebedor = carteiraRecebedor;
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

	@PrePersist
	void onCreate() {
		criadoEm = LocalDateTime.now();
	}

	@PreUpdate
	void onUpdate() {
		atualizadoEm = LocalDateTime.now();
	}
}
