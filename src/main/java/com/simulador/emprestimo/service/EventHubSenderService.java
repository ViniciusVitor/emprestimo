package com.simulador.emprestimo.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class EventHubSenderService {


    private EventHubProducerClient producerClient;

    private final String connectionString = "Endpoint=sb://eventhack.servicebus.windows.net/;SharedAccessKeyName=hack;SharedAccessKey=HeHeVaVqyVkntO2FnjQcs2Ilh/4MUDo4y+AEhKp8z+g=;EntityPath=simulacoes";



    @PostConstruct
    void init(){
        try {
            this.producerClient = new EventHubClientBuilder()
                    .connectionString(connectionString)
                    .buildProducerClient();
        } catch (Exception e) {
            log.error("Erro ao inicializar EventHubProducerClient", e);
            throw e;
        }

    }

    public void enviarEventoJson(String json) {
        EventData eventData = new EventData(json);
        producerClient.send(Collections.singletonList(eventData));
        log.info("Evento enviado com sucesso para o EventHub.");
    }


}
