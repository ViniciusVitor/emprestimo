package com.simulador.emprestimo.model.sqlserver;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_PRODUTO")
    private Long codigoProduto;

    @Column(name = "NO_PRODUTO")
    private String nomeProduto;

    @Column(name = "PC_TAXA_JUROS")
    private BigDecimal taxaJuros;

    @Column(name = "NU_MINIMO_MESES")
    private Integer numeroMinimoMeses;

    @Column(name = "NU_MAXIMO_MESES")
    private Integer numeroMaximoMeses;

    @Column(name = "VR_MINIMO")
    private BigDecimal valorMinimo;

    @Column(name = "VR_MAXIMO")
    private BigDecimal valorMaximo;


}
