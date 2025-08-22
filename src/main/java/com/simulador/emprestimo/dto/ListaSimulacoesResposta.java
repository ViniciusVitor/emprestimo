package com.simulador.emprestimo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ListaSimulacoesResposta {

    private Integer pagina;
    private Integer qtdRegistros;
    private Integer qtdRegistrosPagina;
    private List<RegistroResponse> registros;
}
