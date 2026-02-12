package com.eliasmeyer.sp.domain.model.transferencia;

interface TransferenciaState {

    void reservar(Transferencia transferencia);

    void completar(Transferencia transferencia);

    void falhar(Transferencia transferencia);
}
