package com.eliasmeyer.sp.domain.model.transferencia.eventos;

import com.eliasmeyer.sp.domain.shared.DomainEvent;
import com.eliasmeyer.sp.domain.model.transferencia.TransferenciaId;

import java.time.LocalDateTime;

public class TransferenciaReservadaEvento implements DomainEvent {

    private final TransferenciaId id;
    private final LocalDateTime occurredOn;

    public TransferenciaReservadaEvento(TransferenciaId id, LocalDateTime occurredOn) {
        this.id = id;
        this.occurredOn = occurredOn;
    }

    public TransferenciaId getId() {
        return id;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
