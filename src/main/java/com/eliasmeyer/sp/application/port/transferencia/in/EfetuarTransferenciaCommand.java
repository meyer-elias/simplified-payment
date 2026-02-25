package com.eliasmeyer.sp.application.port.transferencia.in;

import java.math.BigDecimal;

public record EfetuarTransferenciaCommand(String IdPagador, String IdRecebedor, BigDecimal quantia) {
}
