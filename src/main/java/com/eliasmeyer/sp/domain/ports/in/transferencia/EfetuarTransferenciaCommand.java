package com.eliasmeyer.sp.domain.ports.in.transferencia;

import java.math.BigDecimal;

public record EfetuarTransferenciaCommand(String idPagador, String idRecebedor,
                                          BigDecimal quantia) {

}
