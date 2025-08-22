package com.simulador.emprestimo.dto;

import java.time.LocalDate;
import java.util.List;

public record TelemetriaResposta(
        LocalDate dataReferencia,
        List<EndpointMetricas> listaEndpoints
) {
}
