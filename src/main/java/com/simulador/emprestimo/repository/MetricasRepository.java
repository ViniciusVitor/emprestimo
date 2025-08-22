package com.simulador.emprestimo.repository;

import com.simulador.emprestimo.model.EndpointStats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricasRepository {
    private static final Map<String, EndpointStats> metricas = new ConcurrentHashMap<>();

    public static EndpointStats getOrCreate(String endpoint){
        return metricas.computeIfAbsent(endpoint, EndpointStats::new);
    }

    public static Map<String, EndpointStats> getAll(){
        return metricas;
    }
}
