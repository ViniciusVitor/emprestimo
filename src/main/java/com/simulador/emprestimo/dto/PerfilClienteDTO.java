package com.simulador.emprestimo.dto;

import java.math.BigDecimal;

public record PerfilClienteDTO(
        Integer idade,
        BigDecimal rendaMensal,
        BigDecimal valorDesejado,
        Integer prazoMeses,
        Integer scoreCredito

) {
}
