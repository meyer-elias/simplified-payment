package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TransferenciaJpaAdapter implements
	PanacheRepositoryBase<TransferenciaEntity, String> {

	public List<TransferenciaEntity> findByCarteiraIdPaginada(String carteiraId, int pagina,
		int tamanho) {
		return find("""
			SELECT t FROM TransferenciaEntity t
			JOIN FETCH t.carteiraPagador
			JOIN FETCH t.carteiraRecebedor
			WHERE t.carteiraPagador.id = :carteiraId
			   OR t.carteiraRecebedor.id = :carteiraId
			ORDER BY t.atualizadoEm DESC
			""", Map.of("carteiraId", carteiraId))
			.page(pagina, tamanho)
			.list();
	}
}
