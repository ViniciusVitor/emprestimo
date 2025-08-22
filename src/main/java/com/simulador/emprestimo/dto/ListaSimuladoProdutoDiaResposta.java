package com.simulador.emprestimo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListaSimuladoProdutoDiaResposta {

    private LocalDate dataReferencia;
    private List<SimulacaoDia> simulacoes;
}
