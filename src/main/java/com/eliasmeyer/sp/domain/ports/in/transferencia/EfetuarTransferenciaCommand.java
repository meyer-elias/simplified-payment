package com.eliasmeyer.sp.domain.ports.in.transferencia;

import java.math.BigDecimal;

public record EfetuarTransferenciaCommand(String IdPagador, String IdRecebedor, BigDecimal quantia) {
}
