package com.simulador.emprestimo.controller;

import com.simulador.emprestimo.dto.EndpointMetricas;
import com.simulador.emprestimo.dto.TelemetriaResposta;
import com.simulador.emprestimo.repository.MetricasRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/telemetria")
public class TelemetriaController {

    @GetMapping
    public TelemetriaResposta getTelemetria(){
        List<EndpointMetricas> lista = MetricasRepository.getAll()
                .values()
                .stream()
                .map(stat -> new EndpointMetricas(
                        stat.getNomeApi(),
                        stat.getTotalRequisicoes(),
                        stat.getTempoMedio(),
                        stat.getTempoMin(),
                        stat.getTempoMax(),
                        stat.getPercentualSucesso()
                ))
                .collect(Collectors.toList());

        return new TelemetriaResposta(LocalDate.now(), lista);
    }

}
