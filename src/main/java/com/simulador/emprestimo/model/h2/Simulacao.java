package com.simulador.emprestimo.model.h2;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "SIMULACAO")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SIMULACAO")
    private Long idSimulacao;

    @Column(name = "VL_DESEJADO")
    private BigDecimal valorDesejado;

    @Column(name = "PRAZO")
    private Integer prazo;

    @Column(name = "VL_PARCELA_TOTAL_PRICE")
    private BigDecimal valorTotalParcelasPrice;

    @Column(name = "VL_PARCELA_TOTAL_SAC")
    private BigDecimal valorTotalParcelasSac;

    @Column(name = "TX_MEDIA_JUROS_PRICE")
    private BigDecimal taxaMediaJurosPrice;

    @Column(name = "TX_MEDIA_JUROS_SAC")
    private BigDecimal taxaMediaJurosSac;

    @Column(name = "DT_REFERENCIA")
    private LocalDate dataReferencia;

    @Column(name = "CO_PRODUTO")
    private Long codigoProduto;

    @Column(name = "NO_PRODUTO")
    private String descricaoProduto;


//    private BigDecimal taxaMediaJurosPrice;
//    private BigDecimal taxaMediaJurosSac;




}
