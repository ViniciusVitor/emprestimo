package com.simulador.emprestimo.service;

import com.simulador.emprestimo.dto.PerfilClienteDTO;
import com.simulador.emprestimo.dto.ProdutoSugeridoDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SugestaoNovoProdutoService {
    public ProdutoSugeridoDTO sugerirNovoProduto(PerfilClienteDTO perfil){

        BigDecimal taxaBase = new BigDecimal("1.5");

        if (perfil.scoreCredito() > 700) {
            taxaBase = taxaBase.subtract(new BigDecimal("0.3"));
        } else if (perfil.scoreCredito() < 400) {
            taxaBase = taxaBase.add(new BigDecimal("0.5"));
        }

        BigDecimal margem = perfil.rendaMensal().multiply(new BigDecimal("10"));

        return new ProdutoSugeridoDTO("Produto Personalizado", taxaBase, Math.max(6, perfil.prazoMeses() - 6),
                perfil.prazoMeses() + 12,  perfil.valorDesejado().subtract(new BigDecimal("1000")),
                perfil.valorDesejado().add(new BigDecimal("5000"))
                );

    }

}
