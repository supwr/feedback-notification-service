package com.notifier.application.usecase;

import com.notifier.application.dto.event.NotificationEvent;
import com.notifier.application.strategy.NotificationStrategy;
import com.notifier.application.exception.NotificationTypeRequiredException;
import com.notifier.application.exception.UnsupportedNotificationTypeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProcessNotificationUseCase {
    private final List<NotificationStrategy> strategies;

    @Inject
    public ProcessNotificationUseCase(Instance<NotificationStrategy> strategies) {
        this.strategies = new ArrayList<>();
        strategies.forEach(this.strategies::add);
    }

    public ProcessNotificationUseCase(List<NotificationStrategy> strategies) {
        this.strategies = strategies;
    }

    public void execute(NotificationEvent event) {
        if (event == null || event.getType() == null) {
            throw new NotificationTypeRequiredException();
        }

        NotificationStrategy strategy = strategies.stream()
                .filter(candidate -> candidate.supports(event.getType()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedNotificationTypeException(event.getType().name()));

        strategy.send(event);
    }
}
