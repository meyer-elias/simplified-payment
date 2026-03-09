package com.eliasmeyer.sp.core.domain.ports.out.carteira;

import com.eliasmeyer.sp.core.domain.model.carteira.Carteira;
import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import java.util.Optional;

public interface CarteiraOutputPort {

	Optional<Carteira> buscarPor(CarteiraId carteiraId);

	void salvar(Carteira carteira);
}
