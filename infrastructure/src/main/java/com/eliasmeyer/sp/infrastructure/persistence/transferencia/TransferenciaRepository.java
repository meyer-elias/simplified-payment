package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class TransferenciaRepository implements TransferenciaOutputPort {

	private final TransferenciaJpaAdapter transferenciaJpaAdapter;

	private final TransferenciaMapper transferenciaMapper;

	@Inject
	TransferenciaRepository(TransferenciaJpaAdapter transferenciaJpaAdapter,
		TransferenciaMapper transferenciaMapper) {
		this.transferenciaJpaAdapter = transferenciaJpaAdapter;
		this.transferenciaMapper = transferenciaMapper;
	}

	@Override
	public void salvar(Transferencia transferencia) {
		transferenciaJpaAdapter.persist(transferenciaMapper.toEntity(transferencia));
	}

	@Override
	public List<Transferencia> buscarPorCarteiraIdPaginada(CarteiraId carteiraId, int paginaInicia,
		int tamanho) {
		return transferenciaJpaAdapter
			.findByCarteiraIdPaginada(carteiraId.asString(), paginaInicia, tamanho)
			.stream()
			.map(transferenciaMapper::toDomain)
			.toList();

	}
}
