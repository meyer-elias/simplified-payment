package com.eliasmeyer.sp.core.domain.model.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.Dinheiro;
import java.time.LocalDateTime;

/**
 * Classe builder da transferencia utilizada para reconstituir a entidade da base de dados para o
 * mapper da infrastructure.
 * <p>
 * Essa classe é utilizada para que o mapper infra possa reconstituir a entidade de transferencia a
 * partir dos dados da base de dados, permitindo que o mapper infra possa construir a entidade de
 * transferencia.
 * <p>
 * Essa classe não deve ser utilizada diretamente no dominio, apenas no mapper da infra.
 * <p>
 * O objetivo dessa classe é ter uma forma de reconstituir a entidade de transferencia sem que o
 * dominio precise se preocupar com a construção da entidade da infra.
 * <p>
 * OBS: Essa classe não possui um construtor padrão, pois não deve ser construída diretamente no
 * dominio. Classe builder da transferencia como via de uso para o mapper da infrastructure.
 */
public record TransferenciaReconstituicao(TransferenciaId id, Carteira carteiraPagador,
										  Carteira carteiraRecebedor, Dinheiro quantia,
										  LocalDateTime criadoEm, LocalDateTime atualizadoEm,
										  TransferenciaStatus status) {

	public TransferenciaReconstituicao(
		String id,
		Carteira carteiraPagador,
		Carteira carteiraRecebedor,
		Dinheiro quantia,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm,
		TransferenciaStatus status) {
		this(new TransferenciaId(id), carteiraPagador, carteiraRecebedor, quantia, criadoEm,
			atualizadoEm, status);
	}

	public Transferencia reconstituir() {
		return new Transferencia(this);
	}


}
