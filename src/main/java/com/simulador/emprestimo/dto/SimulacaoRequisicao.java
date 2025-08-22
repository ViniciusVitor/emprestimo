package com.simulador.emprestimo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SimulacaoRequisicao {

    private BigDecimal valorDesejado;
    private Integer prazo;
}
