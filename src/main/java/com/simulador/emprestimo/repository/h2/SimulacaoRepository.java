package com.simulador.emprestimo.repository.h2;

import com.simulador.emprestimo.model.h2.Simulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimulacaoRepository extends JpaRepository<Simulacao, Long> {

    //List<Simulacao> findByDataReferencia();




    @Query(value = """
    SELECT 
        s.DT_REFERENCIA AS dataReferencia,
        s.CO_PRODUTO AS codigoProduto,
        s.NO_PRODUTO AS descricaoProduto,
        AVG(s.TX_MEDIA_JUROS_PRICE) AS mediaTaxaJurosPrice,
        SUM(s.VL_PARCELA_TOTAL_PRICE) AS totalParcelasPrice,
        SUM(s.VL_DESEJADO) AS somaValorDesejado,
        SUM(s.PRAZO) AS qtdTotalPrazo
    FROM SIMULACAO s
    GROUP BY s.DT_REFERENCIA, s.CO_PRODUTO, s.NO_PRODUTO
    """, nativeQuery = true)
    List<Object[]> buscarAgrupamentoSimulacoes();



}
