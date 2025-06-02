package com.mercadolivre.scheduler.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.scheduler.application.dto.TemperatureScheduleInput;
import com.mercadolivre.scheduler.application.usecase.ScheduleTemperatureUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SolicitaTemperaturaConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final ScheduleTemperatureUseCase scheduleTemperatureUseCase;

    @Value("${aws.sqs.temperature.queue-url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 1000)
    public void receiveMessages() {
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(20)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

            for (Message message : messages) {
                try {
                    TemperatureScheduleInput input = objectMapper.readValue(message.body(), TemperatureScheduleInput.class);
                    scheduleTemperatureUseCase.execute(input);
                    
                    // Delete message after successful processing
                    sqsClient.deleteMessage(builder -> builder
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build());
                    
                } catch (Exception e) {
                    log.error("Error processing message: {}", message.body(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error receiving messages from SQS", e);
        }
    }
} 