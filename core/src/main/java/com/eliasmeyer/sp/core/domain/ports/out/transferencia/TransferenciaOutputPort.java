package com.eliasmeyer.sp.core.domain.ports.out.transferencia;

import com.eliasmeyer.sp.core.domain.model.carteira.CarteiraId;
import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import java.util.List;

public interface TransferenciaOutputPort {

	void salvar(Transferencia transferencia);

	List<Transferencia> buscarPorCarteiraIdPaginada(CarteiraId carteiraId, int paginaInicial,
		int tamanho);
}
