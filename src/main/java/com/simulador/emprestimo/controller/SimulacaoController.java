package com.simulador.emprestimo.controller;

import com.simulador.emprestimo.dto.ListaSimulacoesResposta;
import com.simulador.emprestimo.dto.ListaSimuladoProdutoDiaResposta;
import com.simulador.emprestimo.dto.SimulacaoRequisicao;
import com.simulador.emprestimo.dto.SimulacaoResposta;
import com.simulador.emprestimo.service.SimulacaoService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/emprestimo")
public class SimulacaoController {

    private final SimulacaoService simulacaoService;


    @Autowired
    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }


    @PostMapping
    public ResponseEntity<Object> fazerSimulacaoEmprestimo(@RequestBody SimulacaoRequisicao simulacaoRequisicao) {
        SimulacaoResposta retorno = simulacaoService.fazerSimulacaoEmprestimo(simulacaoRequisicao);
        if (retorno != null) {
            return ResponseEntity.ok(retorno);
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensagem", "Simulação inválida"));
        }
    }

    @GetMapping("/lista/simulacao")
    public ListaSimulacoesResposta listarSimulacoes(@ParameterObject Pageable pageable){
        return simulacaoService.listarSimulacoes(pageable);
    }

    @GetMapping("/lista/simulacao/produto/dia")
    public List<ListaSimuladoProdutoDiaResposta> listarSimulacoesPorProdutoEDia(){
        return simulacaoService.listarSimulacoesPorProdutoEDia();
    }


}
