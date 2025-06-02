package com.mercadolivre.webapp.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.webapp.application.dto.ProcessTemperatureResultInput;
import com.mercadolivre.webapp.application.usecase.ProcessTemperatureResultUseCase;
import com.mercadolivre.webapp.infrastructure.sqs.message.TemperatureResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsTemperatureResultConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final ProcessTemperatureResultUseCase processTemperatureResultUseCase;

    @Value("${aws.sqs.temperature.result-queue-url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 5000) // Executa a cada 5 segundos
    public void consumeMessages() {
        log.debug("Starting to poll messages from queue...");
        try {
            // Recebe até 10 mensagens
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            log.debug("Received {} messages from queue", messages.size());

            for (Message message : messages) {
                processMessage(message);
                deleteMessage(message);
            }
        } catch (Exception e) {
            log.error("Error consuming messages from SQS: {}", e.getMessage(), e);
        }
    }

    private void processMessage(Message message) {
        try {
            log.debug("Processing message with ID: {}", message.messageId());
            
            TemperatureResultMessage resultMessage = objectMapper.readValue(
                    message.body(), 
                    TemperatureResultMessage.class
            );

            ProcessTemperatureResultInput input = convertToInput(resultMessage);
            processTemperatureResultUseCase.execute(input);

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }

    private ProcessTemperatureResultInput convertToInput(TemperatureResultMessage message) {
        return ProcessTemperatureResultInput.builder()
                .scheduleId(message.getScheduleId())
                .cityId(message.getCityId())
                .cityName(message.getCityName())
                .state(message.getState())
                .forecasts(message.getForecasts().stream()
                        .map(this::convertForecast)
                        .collect(Collectors.toList()))
                .build();
    }

    private ProcessTemperatureResultInput.ForecastData convertForecast(TemperatureResultMessage.ForecastData forecast) {
        return ProcessTemperatureResultInput.ForecastData.builder()
                .date(forecast.getDate())
                .condition(forecast.getCondition())
                .minTemperature(forecast.getMinTemperature())
                .maxTemperature(forecast.getMaxTemperature())
                .uvIndex(forecast.getUvIndex())
                .build();
    }

    private void deleteMessage(Message message) {
        try {
            log.debug("Deleting message with ID: {}", message.messageId());
            
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();

            sqsClient.deleteMessage(deleteRequest);
            log.debug("Message deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting message: {}", e.getMessage(), e);
        }
    }
} 