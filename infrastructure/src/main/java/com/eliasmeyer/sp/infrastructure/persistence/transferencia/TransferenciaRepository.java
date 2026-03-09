package com.eliasmeyer.sp.infrastructure.persistence.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.model.transferencia.TransferenciaId;
import com.eliasmeyer.sp.core.domain.ports.out.transferencia.TransferenciaOutputPort;
import com.eliasmeyer.sp.infrastructure.persistence.carteira.CarteiraRepository;
import jakarta.inject.Inject;
import java.util.Optional;

public class TransferenciaRepository implements TransferenciaOutputPort {

	private final TransferenciaJpaAdapter transferenciaJpaAdapter;

	private final TransferenciaMapper transferenciaMapper;

	private final CarteiraRepository carteiraRepository;

	@Inject
	TransferenciaRepository(TransferenciaJpaAdapter transferenciaJpaAdapter,
		TransferenciaMapper transferenciaMapper, CarteiraRepository carteiraRepository) {
		this.transferenciaJpaAdapter = transferenciaJpaAdapter;
		this.transferenciaMapper = transferenciaMapper;
		this.carteiraRepository = carteiraRepository;
	}

	@Override
	public void salvar(Transferencia transferencia) {
		transferenciaMapper.toEntity(transferencia);
	}

	public Optional<Transferencia> buscarPorId(TransferenciaId transferenciaId) {
		return transferenciaJpaAdapter.findById(transferenciaId.asString())
			.map(entity -> {
				Carteira cPagador = buscarCarteiraByCarteiraId(entity.getCarteiraIdPagador());
				Carteira cRecebedor = buscarCarteiraByCarteiraId(entity.getCarteiraIdRecebedor());
				return transferenciaMapper.toDomain(entity, cPagador, cRecebedor);
			});
	}

	private Carteira buscarCarteiraByCarteiraId(String carteiraId) {
		return carteiraRepository.buscarPor(new CarteiraId(carteiraId))
			.orElseThrow(() -> new IllegalArgumentException(
				String.format("Carteira não localizada com o id [%s]", carteiraId)));
	}
}
