package com.simulador.emprestimo.interceptor;

import com.simulador.emprestimo.repository.MetricasRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.List;

@Component
public class MetricsInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        // Lista de URIs que devem ser ignoradas
        List<String> urisIgnoradas = List.of(
                "/swagger-ui", "/swagger-ui/swagger-ui.css", "/v3/api-docs/swagger-config",
                "/v3/api-docs", "/swagger-ui/swagger-initializer.js", "/swagger-ui/index.css",
                "/swagger-ui/swagger-ui-standalone-preset.js",  "/swagger-ui/index.html",
                "/swagger-ui/swagger-ui-bundle.js", "/swagger-resources", "/h2-console"
        );

        boolean ignorar = urisIgnoradas.stream().anyMatch(uri::startsWith);

        if (!ignorar) {
            request.setAttribute("startTime", System.currentTimeMillis());
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object startTimeAttr = request.getAttribute("startTime");

        if (startTimeAttr instanceof Long) {
            long startTime = (long) startTimeAttr;
            long duration = System.currentTimeMillis() - startTime;

            String endpoint = request.getRequestURI();
            boolean sucesso = response.getStatus() == 200;

            MetricasRepository.getOrCreate(endpoint).registrarRequisicao(duration, sucesso);
        }

    }

}
