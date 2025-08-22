package com.simulador.emprestimo.dto;

import java.math.BigDecimal;

public record ProdutoSugeridoDTO(

        String nomeProduto,
        BigDecimal taxaJuros,
        Integer numeroMinimoMeses,
        Integer numeroMaximoMeses,
        BigDecimal valorMinimo,
        BigDecimal valorMaximo

) {
}
