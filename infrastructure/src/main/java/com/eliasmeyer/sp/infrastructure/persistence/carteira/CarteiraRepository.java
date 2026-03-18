package com.eliasmeyer.sp.infrastructure.persistence.carteira;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.ports.out.carteira.CarteiraOutputPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class CarteiraRepository implements CarteiraOutputPort {

	private final CarteiraJpaAdapter carteiraJPAAdapter;

	private final CarteiraMapper carteiraMapper;

	@Inject
	public CarteiraRepository(CarteiraJpaAdapter carteiraJPAAdapter,
		CarteiraMapper carteiraMapper) {
		this.carteiraJPAAdapter = carteiraJPAAdapter;
		this.carteiraMapper = carteiraMapper;
	}

	@Override
	public void salvar(Carteira carteira) {
		CarteiraEntity entity = carteiraMapper.toEntity(carteira);
		carteiraJPAAdapter.persist(entity);
	}

	@Override
	public Optional<Carteira> buscarPor(CarteiraId carteiraId) {
		return carteiraJPAAdapter.findByIdOptional(carteiraId.asString())
			.map(carteiraMapper::toDomain);
	}
}
