package com.simulador.emprestimo.dto;

public record EndpointMetricas(
        String nomeApi,
        Long qtdRequisicoes,
        Double tempoMedio,
        Long tempoMinimo,
        Long tempoMaxino,
        Double percentualSucesso
) {
}
