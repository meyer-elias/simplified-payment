package com.eliasmeyer.sp.domain.model.transferencia;

class TransferenciaReservada implements TransferenciaState {

    @Override
    public void reservar(Transferencia transferencia) {
        throw new IllegalStateException("Não pode reservar uma transferência já reservada!");
    }

    @Override
    public void completar(Transferencia transferencia) {
        transferencia.mudarState(new TransferenciaRealizada());
    }

    @Override
    public void falhar(Transferencia transferencia) {
        transferencia.mudarState(new TransferenciaFalhada());
    }
}
