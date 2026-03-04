package com.eliasmeyer.sp.core.domain.model.transferencia.eventos;

import com.eliasmeyer.sp.core.domain.model.transferencia.Transferencia;
import com.eliasmeyer.sp.core.domain.shared.DomainEvent;
import java.time.LocalDateTime;

public record TransferenciaReservadaEvento(Transferencia transferencia,
										   LocalDateTime occurredOn) implements DomainEvent {

}
