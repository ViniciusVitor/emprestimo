package com.simulador.emprestimo.service;

import com.nimbusds.jose.shaded.gson.Gson;
import com.simulador.emprestimo.dto.*;
import com.simulador.emprestimo.model.sqlserver.Produto;
import com.simulador.emprestimo.model.h2.Simulacao;
import com.simulador.emprestimo.repository.sqlserver.ProdutoRepository;
import com.simulador.emprestimo.repository.h2.SimulacaoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SimulacaoService {

    private final ProdutoRepository produtoRepository;

    private final SimulacaoRepository simulacaoRepository;

    private final EventHubSenderService eventHubSenderService;

    @Autowired
    public SimulacaoService(ProdutoRepository produtoRepository, SimulacaoRepository simulacaoRepository, EventHubSenderService eventHubSenderService) {
        this.produtoRepository = produtoRepository;
        this.simulacaoRepository = simulacaoRepository;
        this.eventHubSenderService = eventHubSenderService;
    }

    public SimulacaoResposta fazerSimulacaoEmprestimo(SimulacaoRequisicao simulacaoRequisicao){
        log.info("Iniciando simulaão de emprestimo para o valor: " + simulacaoRequisicao.getValorDesejado()
        + " no prazo de: " + simulacaoRequisicao.getPrazo() + " vezes.");

        TaxaValorMedio taxaValorMedioJuros = new TaxaValorMedio();
        Produto produto = null;
        SimulacaoResposta respostaSimulacao = null;
        List<ResultadoSimulacao> resultadoSimulacoes = new ArrayList<>();
        List<Produto> listaDeProdutos =  produtoRepository.findProdutoByValorAndPrazo(simulacaoRequisicao.getValorDesejado(), simulacaoRequisicao.getPrazo());
        if(listaDeProdutos != null && !listaDeProdutos.isEmpty()){
            produto = listaDeProdutos.get(0);

            resultadoSimulacoes.add(ResultadoSimulacao.builder()
                    .tipo("PRICE")
                    .parcelas(calcularPrice(simulacaoRequisicao, produto, taxaValorMedioJuros))
                    .build());
            resultadoSimulacoes.add(ResultadoSimulacao.builder()
                    .tipo("SAC")
                    .parcelas(calcularSAC(simulacaoRequisicao, produto, taxaValorMedioJuros))
                    .build());

            // persistir no banco essa informacao
            Simulacao simulacaoSalva = salvarSimulacao(simulacaoRequisicao, produto, taxaValorMedioJuros.getTaxaMediaJurosPrice(),
                    taxaValorMedioJuros.getTaxaMediaJurosSac(), taxaValorMedioJuros.getValorTotalParcelasPrice(),
                    taxaValorMedioJuros.getValorTotalParcelasSac());

            respostaSimulacao =  SimulacaoResposta.builder()
                    .idSimulacao(simulacaoSalva.getIdSimulacao())
                    .codigoProduto(produto.getCodigoProduto())
                    .descricaoProduto(produto.getNomeProduto())
                    .taxaJuros(produto.getTaxaJuros())
                    .resultadoSimulacao(resultadoSimulacoes)
                    .build();

            Gson gson = new Gson();
            String jsonSimulacao = gson.toJson(respostaSimulacao);

            // enviar evento
            eventHubSenderService.enviarEventoJson(jsonSimulacao);
            log.info("Evento enviado para o EventHub: " + jsonSimulacao);
        }

        return respostaSimulacao;
    }

    public ListaSimulacoesResposta listarSimulacoes(Pageable pageable){

        try {

            Page<Simulacao> paginaSimulacao = simulacaoRepository.findAll(pageable);
            List<RegistroResponse> listaRegistros = new ArrayList<>();

            for(Simulacao simu : paginaSimulacao.getContent()){
                listaRegistros.add(RegistroResponse.builder()
                        .idSimulacao(simu.getIdSimulacao())
                        .valorTotalParcelasPrice(simu.getValorTotalParcelasPrice())
                        .valorTotalParcelasSac(simu.getValorTotalParcelasSac())
                        .valorDesejado(simu.getValorDesejado())
                        .prazo(simu.getPrazo())
                        .build());
            }

            return ListaSimulacoesResposta.builder()
                    .qtdRegistrosPagina(paginaSimulacao.getSize())
                    .qtdRegistros((int) paginaSimulacao.getTotalElements())
                    .pagina(paginaSimulacao.getNumber())
                    .registros(listaRegistros)
                    .build();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }



    public List<ParcelaResposta> calcularPrice(SimulacaoRequisicao simulacaoRequisicao, Produto produto,
                                               TaxaValorMedio taxaValorMedioJuros) {

        List<ParcelaResposta> listaDeParcelasPrice = new ArrayList<>();

        BigDecimal taxaJuros = produto.getTaxaJuros();
        int prazo = simulacaoRequisicao.getPrazo();
        BigDecimal valorDesejado = simulacaoRequisicao.getValorDesejado();

        // Cálculo da prestação fixa
        BigDecimal base = BigDecimal.ONE.add(taxaJuros);
        BigDecimal potencia = BigDecimal.ONE.divide(base.pow(prazo, MathContext.DECIMAL128), MathContext.DECIMAL128);
        BigDecimal divisor = BigDecimal.ONE.subtract(potencia);
        BigDecimal valorPrestacao = valorDesejado.multiply(taxaJuros).divide(divisor, 2, RoundingMode.HALF_UP);

        BigDecimal saldoDevedor = valorDesejado;

        BigDecimal taxaMediaJurosTemp = BigDecimal.ZERO;
        BigDecimal valorTotalPrestacao = BigDecimal.ZERO;

        for (int parcela = 1; parcela <= prazo; parcela++) {
            BigDecimal jurosParcela = saldoDevedor.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);
            BigDecimal amortizacao = valorPrestacao.subtract(jurosParcela).setScale(2, RoundingMode.HALF_UP);
            saldoDevedor = saldoDevedor.subtract(amortizacao).setScale(2, RoundingMode.HALF_UP);

            listaDeParcelasPrice.add(
                    ParcelaResposta.builder()
                            .numero(parcela)
                            .valorPrestacao(valorPrestacao)
                            .valorAmortizacao(amortizacao)
                            .valorJuros(jurosParcela)
                            .build()
            );

            taxaMediaJurosTemp = taxaMediaJurosTemp.add(taxaJuros);
            valorTotalPrestacao = valorTotalPrestacao.add(valorPrestacao);
        }
        taxaValorMedioJuros.setTaxaMediaJurosPrice(taxaMediaJurosTemp.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP));
        taxaValorMedioJuros.setValorTotalParcelasPrice(valorTotalPrestacao);
        return listaDeParcelasPrice;
    }


    public List<ParcelaResposta> calcularSAC(SimulacaoRequisicao simulacaoRequisicao, Produto produto,
                                             TaxaValorMedio taxaValorMedioJuros) {

        List<ParcelaResposta> listaDeParcelasSAC = new ArrayList<>();

        BigDecimal taxaJuros = produto.getTaxaJuros();
        int prazo = simulacaoRequisicao.getPrazo();
        BigDecimal valorDesejado = simulacaoRequisicao.getValorDesejado();

        // Amortização constante
        BigDecimal amortizacao = valorDesejado.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        BigDecimal saldoDevedor = valorDesejado;

        BigDecimal taxaMediaJurosTemp = BigDecimal.ZERO;
        BigDecimal valorTotalPrestacao = BigDecimal.ZERO;

        for (int parcela = 1; parcela <= prazo; parcela++) {
            BigDecimal jurosParcela = saldoDevedor.multiply(taxaJuros).setScale(2, RoundingMode.HALF_UP);
            BigDecimal valorPrestacao = amortizacao.add(jurosParcela).setScale(2, RoundingMode.HALF_UP);
            saldoDevedor = saldoDevedor.subtract(amortizacao).setScale(2, RoundingMode.HALF_UP);

            listaDeParcelasSAC.add(
                    ParcelaResposta.builder()
                            .numero(parcela)
                            .valorPrestacao(valorPrestacao)
                            .valorAmortizacao(amortizacao)
                            .valorJuros(jurosParcela)
                            .build()
            );

            taxaMediaJurosTemp = taxaMediaJurosTemp.add(taxaJuros);
            valorTotalPrestacao = valorTotalPrestacao.add(valorPrestacao);
        }
        taxaValorMedioJuros.setTaxaMediaJurosSac(taxaMediaJurosTemp.divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP));
        taxaValorMedioJuros.setValorTotalParcelasSac(valorTotalPrestacao);
        return listaDeParcelasSAC;
    }

    public Simulacao salvarSimulacao(SimulacaoRequisicao simulacaoRequisicao, Produto produto,
                                BigDecimal taxaMediaJurosPrice, BigDecimal taxaMediaJurosSac,
                                BigDecimal valorTotalParcelasPrice, BigDecimal valorTotalParcelasSac){



        Simulacao novaSimulacao = new Simulacao();
        novaSimulacao.setPrazo(simulacaoRequisicao.getPrazo());
        novaSimulacao.setCodigoProduto(produto.getCodigoProduto());
        novaSimulacao.setTaxaMediaJurosPrice(taxaMediaJurosPrice);
        novaSimulacao.setTaxaMediaJurosSac(taxaMediaJurosSac);
        novaSimulacao.setDataReferencia(LocalDate.now());
        novaSimulacao.setDescricaoProduto(produto.getNomeProduto());
        novaSimulacao.setValorDesejado(simulacaoRequisicao.getValorDesejado());
        novaSimulacao.setValorTotalParcelasSac(valorTotalParcelasSac);
        novaSimulacao.setValorTotalParcelasPrice(valorTotalParcelasPrice);

        log.info("Salvar simulação no banco: " + novaSimulacao);

        return simulacaoRepository.save(novaSimulacao);
    }


    public List<ListaSimuladoProdutoDiaResposta> listarSimulacoesPorProdutoEDia(){

        List<Object[]> resultados = simulacaoRepository.buscarAgrupamentoSimulacoes();

        Map<LocalDate, List<SimulacaoDia>> agrupadoPorData = new HashMap<>();

        for (Object[] obj : resultados) {
            LocalDate dataReferencia = ((java.sql.Date) obj[0]).toLocalDate();
            Long codigoProduto = ((Number) obj[1]).longValue();
            String descricaoProduto = (String) obj[2];
            BigDecimal mediaTaxaJurosPrice = (BigDecimal) obj[3];
            BigDecimal totalParcelasPrice = (BigDecimal) obj[4];
            BigDecimal somaValorDesejado = (BigDecimal) obj[5];
            Long totalTotalPrazo = (Long) obj[6];

            BigDecimal mediaParcelasPrice = totalParcelasPrice.divide(new BigDecimal(totalTotalPrazo), RoundingMode.HALF_UP);
            BigDecimal totalCredito = mediaParcelasPrice.multiply(new BigDecimal(totalTotalPrazo));

            SimulacaoDia simulacaoDia = new SimulacaoDia(
                    codigoProduto,
                    descricaoProduto,
                    mediaTaxaJurosPrice,
                    mediaParcelasPrice,
                    somaValorDesejado,
                    totalCredito
            );

            agrupadoPorData.computeIfAbsent(dataReferencia, k -> new ArrayList<>()).add(simulacaoDia);
        }

        return agrupadoPorData.entrySet().stream()
                .map(entry -> new ListaSimuladoProdutoDiaResposta(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());



    }

}
