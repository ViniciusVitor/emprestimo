package com.simulador.emprestimo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TaxaValorMedio {

    BigDecimal taxaMediaJurosPrice;
    BigDecimal taxaMediaJurosSac;
    BigDecimal valorTotalParcelasPrice;
    BigDecimal valorTotalParcelasSac;
}
