package com.notifier.application.usecase;

import com.notifier.application.dto.event.NotificationEvent;
import com.notifier.application.strategy.NotificationStrategy;
import com.notifier.application.exception.UnsupportedNotificationTypeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProcessNotificationUseCaseTest {

    @Test
    void shouldDispatchEmailNotificationToSupportedStrategy() {
        RecordingStrategy strategy = new RecordingStrategy();
        ProcessNotificationUseCase useCase = new ProcessNotificationUseCase(List.of(strategy));
        NotificationEvent event = NotificationEvent.builder()
                .notificationId("notification-123")
                .type(NotificationEvent.NotificationType.EMAIL)
                .build();

        useCase.execute(event);

        assertEquals("notification-123", strategy.event.getNotificationId());
    }

    @Test
    void shouldRejectUnsupportedType() {
        ProcessNotificationUseCase useCase = new ProcessNotificationUseCase(List.of());
        NotificationEvent event = NotificationEvent.builder()
                .type(NotificationEvent.NotificationType.UNSUPPORTED)
                .build();

        assertThrows(UnsupportedNotificationTypeException.class, () -> useCase.execute(event));
    }

    private static class RecordingStrategy implements NotificationStrategy {
        private NotificationEvent event;

        @Override
        public boolean supports(NotificationEvent.NotificationType type) {
            return type == NotificationEvent.NotificationType.EMAIL;
        }

        @Override
        public void send(NotificationEvent event) {
            this.event = event;
        }
    }
}
