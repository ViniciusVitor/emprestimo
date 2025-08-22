package com.simulador.emprestimo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RegistroResponse {

    private Long idSimulacao;
    private BigDecimal valorDesejado;
    private Integer prazo;
    private BigDecimal valorTotalParcelasPrice;
    private BigDecimal valorTotalParcelasSac;
}
