package com.eliasmeyer.sp.domain.model.transferencia.eventos;

import com.eliasmeyer.sp.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.domain.shared.DomainEvent;
import java.time.LocalDateTime;

public record TransferenciaReservadaEvento(Transferencia transferencia,
                                           LocalDateTime occurredOn) implements DomainEvent {

}
