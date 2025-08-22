package com.simulador.emprestimo.repository.sqlserver;

import com.simulador.emprestimo.model.sqlserver.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {


    @Query("SELECT p FROM Produto p " +
            "WHERE :valorDesejado BETWEEN p.valorMinimo AND p.valorMaximo " +
            "AND :prazo BETWEEN p.numeroMinimoMeses AND p.numeroMaximoMeses")
    List<Produto> findProdutoByValorAndPrazo(@Param("valorDesejado") BigDecimal valorDesejado, @Param("prazo") Integer prazo);


}
