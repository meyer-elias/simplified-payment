package com.eliasmeyer.sp.domain.model.transferencia;

class TransferenciaFalhada implements TransferenciaState {

    @Override
    public void falhar(Transferencia transferencia) {
        throw new IllegalStateException("Já falhada!");
    }

    @Override
    public void completar(Transferencia transferencia) {
        throw new IllegalStateException("Transferência falhou!");
    }

    @Override
    public void reservar(Transferencia transferencia) {
        throw new IllegalStateException("Transferência falhou!");
    }
}
