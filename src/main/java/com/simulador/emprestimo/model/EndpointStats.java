package com.simulador.emprestimo.model;

import java.util.concurrent.atomic.AtomicLong;

public class EndpointStats {
    private final String nomeApi;
    private final AtomicLong totalRequisicoes = new AtomicLong();
    private final AtomicLong sucesso = new AtomicLong();
    private final AtomicLong tempoTotal = new AtomicLong();
    private final AtomicLong tempoMin = new AtomicLong(Long.MIN_VALUE);
    private final AtomicLong tempoMax = new AtomicLong();

    public EndpointStats(String nomeApi) {
        this.nomeApi = nomeApi;
    }

    public void registrarRequisicao(long tempoExecucao, boolean sucesso){
        totalRequisicoes.incrementAndGet();
        tempoTotal.addAndGet(tempoExecucao);
        tempoMin.updateAndGet(prev -> Math.min(prev, tempoExecucao));
        tempoMax.updateAndGet(prev -> Math.max(prev, tempoExecucao));
        if (sucesso) this.sucesso.incrementAndGet();
    }

    public String getNomeApi(){
        return nomeApi;
    }

    public Long getTotalRequisicoes(){
        return totalRequisicoes.get();
    }

    public Double getTempoMedio(){
        return totalRequisicoes.get() == 0 ? 0 : (double) tempoTotal.get() / totalRequisicoes.get();
    }

    public Long getTempoMin(){
        return tempoMin.get() == Long.MAX_VALUE ? 0 : tempoMin.get();

    }

    public Long getTempoMax(){
        return tempoMax.get();

    }

    public Double getPercentualSucesso(){
        return totalRequisicoes.get() == 0 ? 0 : (double) sucesso.get() / totalRequisicoes.get();
    }
}
