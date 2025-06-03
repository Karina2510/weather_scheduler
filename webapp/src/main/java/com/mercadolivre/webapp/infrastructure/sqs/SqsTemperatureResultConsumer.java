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
                try {
                    processMessage(message);
                    // Só deleta a mensagem se o processamento foi bem sucedido
                    deleteMessage(message);
                    log.info("Message {} processed and deleted successfully", message.messageId());
                } catch (Exception e) {
                    log.error("Error processing message {}, it will return to the queue: {}", 
                            message.messageId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error consuming messages from SQS: {}", e.getMessage(), e);
        }
    }

    private void processMessage(Message message) throws Exception {
        log.debug("Processing message with ID: {}", message.messageId());
        
        TemperatureResultMessage resultMessage = objectMapper.readValue(
                message.body(), 
                TemperatureResultMessage.class
        );

        ProcessTemperatureResultInput input = convertToInput(resultMessage);
        processTemperatureResultUseCase.execute(input);
    }

    private ProcessTemperatureResultInput convertToInput(TemperatureResultMessage message) {
        return ProcessTemperatureResultInput.builder()
                .scheduleId(message.getScheduleId())
                .cityId(message.getCityId())
                .cityName(message.getCityName())
                .state(message.getState())
                .requestDate(message.getRequestDate())
                .forecasts(message.getForecasts().stream()
                        .map(this::convertForecast)
                        .collect(Collectors.toList()))
                .wave(message.getWave() != null ? convertWave(message.getWave()) : null)
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

    private ProcessTemperatureResultInput.WaveData convertWave(TemperatureResultMessage.WaveData wave) {
        return ProcessTemperatureResultInput.WaveData.builder()
                .date(wave.getDate())
                .morning(convertWavePeriod(wave.getMorning()))
                .afternoon(convertWavePeriod(wave.getAfternoon()))
                .night(convertWavePeriod(wave.getNight()))
                .build();
    }

    private ProcessTemperatureResultInput.WavePeriodData convertWavePeriod(TemperatureResultMessage.WavePeriodData period) {
        if (period == null) return null;
        return ProcessTemperatureResultInput.WavePeriodData.builder()
                .time(period.getTime())
                .agitation(period.getAgitation())
                .waveHeight(period.getWaveHeight())
                .waveDirection(period.getWaveDirection())
                .windSpeed(period.getWindSpeed())
                .windDirection(period.getWindDirection())
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
            throw e; // Propaga o erro para garantir que a mensagem volte para a fila
        }
    }
} 