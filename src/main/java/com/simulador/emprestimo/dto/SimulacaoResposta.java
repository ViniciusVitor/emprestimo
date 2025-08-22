package com.simulador.emprestimo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class SimulacaoResposta {

    private Long idSimulacao;
    private Long codigoProduto;
    private String descricaoProduto;
    private BigDecimal taxaJuros;
    private List<ResultadoSimulacao> resultadoSimulacao;
}
