package com.mercadolivre.scheduler.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.scheduler.infrastructure.sqs.message.TemperatureResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class SqsTemperatureResultSender {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.temperature.result-queue-url}")
    private String queueUrl;

    public void send(TemperatureResultMessage message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMessageRequest);
            
            log.info("Temperature result sent to SQS for city: {} - {}", message.getCityName(), message.getState());
        } catch (Exception e) {
            log.error("Error sending temperature result to SQS for city ID: {}", message.getCityId(), e);
            throw new RuntimeException("Error sending message to SQS", e);
        }
    }
} 