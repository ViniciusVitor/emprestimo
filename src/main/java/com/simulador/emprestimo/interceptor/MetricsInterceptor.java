package com.simulador.emprestimo.interceptor;

import com.simulador.emprestimo.repository.MetricasRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse reponse, Object handler){
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        long startTime = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        String endpoint = request.getRequestURI();
        boolean sucesso = response.getStatus() == 200;

        MetricasRepository.getOrCreate(endpoint).registrarRequisicao(duration, sucesso);
    }
}
