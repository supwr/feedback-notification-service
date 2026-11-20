package com.notifier.infrastructure.messaging;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifier.application.dto.event.NotificationEvent;
import com.notifier.application.usecase.ProcessNotificationUseCase;
import com.notifier.infrastructure.exception.SqsMessageProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SqsNotificationHandler implements RequestHandler<SQSEvent, Void> {
    private final ProcessNotificationUseCase useCase;
    private final ObjectMapper objectMapper;

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        ObjectMapper mapper = objectMapper.copy()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                NotificationEvent notification = mapper.readValue(message.getBody(), NotificationEvent.class);
                useCase.execute(notification);
            } catch (Exception exception) {
                throw new SqsMessageProcessingException(message.getMessageId(), exception);
            }
        }
        return null;
    }
}
