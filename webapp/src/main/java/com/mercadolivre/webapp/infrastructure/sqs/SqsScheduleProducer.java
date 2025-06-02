package com.mercadolivre.webapp.infrastructure.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolivre.webapp.domain.Schedule;
import com.mercadolivre.webapp.domain.port.ScheduleQueue;
import com.mercadolivre.webapp.infrastructure.sqs.message.TemperatureScheduleMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsScheduleProducer implements ScheduleQueue {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.temperature.queue-url}")
    private String queueUrl;

    @Override
    public void send(Schedule schedule) {
        try {
            TemperatureScheduleMessage message = TemperatureScheduleMessage.builder()
                    .scheduleId(schedule.getId())
                    .cityId(schedule.getCityId())
                    .scheduleDate(schedule.getScheduleDate())
                    .build();

            String messageBody = objectMapper.writeValueAsString(message);

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMessageRequest);
            log.info("Schedule sent to SQS queue: {}", messageBody);
        } catch (Exception e) {
            log.error("Error sending schedule to SQS: {}", e.getMessage(), e);
            throw new RuntimeException("Error sending schedule to SQS", e);
        }
    }
} 