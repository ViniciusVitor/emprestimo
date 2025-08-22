package com.simulador.emprestimo.controller;

import com.simulador.emprestimo.dto.PerfilClienteDTO;
import com.simulador.emprestimo.dto.ProdutoSugeridoDTO;
import com.simulador.emprestimo.service.SugestaoNovoProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sugestao/novo/produto")
public class SugestaoNovoProdutoController {

    private final SugestaoNovoProdutoService sugestaoNovoProdutoService;

    @Autowired
    public SugestaoNovoProdutoController(SugestaoNovoProdutoService sugestaoNovoProdutoService) {
        this.sugestaoNovoProdutoService = sugestaoNovoProdutoService;
    }

    @PostMapping()
    public ResponseEntity<ProdutoSugeridoDTO> sugerirEmprestimo(@RequestBody PerfilClienteDTO perfil) {
        ProdutoSugeridoDTO sugestao = sugestaoNovoProdutoService.sugerirNovoProduto(perfil);
        return ResponseEntity.ok(sugestao);
    }


}
