package com.eliasmeyer.sp.application.shared;

import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.shared.DomainEvent;
import com.eliasmeyer.sp.domain.shared.DomainEventHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of a domain event dispatcher.
 * <p>
 * Manages the registration and dispatch of domain events to their respective handlers.
 * Handlers are registered at construction time and cannot be modified afterwards.
 * Event dispatch is thread-safe and continues even if individual handlers fail.
 * </p>
 */
public class DefaultDomainEventDispatcher {

    private static final List<DomainEventHandler<? extends DomainEvent>> EMPTY_HANDLER_LIST = List.of();

    private final AppLogger appLogger;
    private final Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers;

    /**
     * Creates a new domain event dispatcher with the provided handlers.
     * <p>
     * Handlers are indexed by their event type for efficient lookup during dispatch.
     * Once initialized, the dispatcher cannot be modified (immutable handlers registry).
     * </p>
     *
     * @param appLogger     the logger for dispatch-related logs
     * @param eventHandlers list of handlers to register; cannot be null or contain null elements
     * @throws NullPointerException if eventHandlers is null or contains null handlers
     */
    public DefaultDomainEventDispatcher(AppLogger appLogger, List<DomainEventHandler<? extends DomainEvent>> eventHandlers) {
        Objects.requireNonNull(eventHandlers, "Event handlers list cannot be null");
        this.appLogger = appLogger;
        this.handlers = new ConcurrentHashMap<>();

        for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
            Objects.requireNonNull(handler, "Event handler cannot be null");
            handlers.computeIfAbsent(handler.eventType(), k -> new ArrayList<>()).add(handler);
        }

        // Make handlers map immutable after construction
        handlers.replaceAll((k, v) -> Collections.unmodifiableList(v));
        appLogger.info("DomainEventDispatcher initialized with {} handlers for {} event types",
                eventHandlers.size(), handlers.size());
    }

    /**
     * Dispatches a list of domain events to their registered handlers.
     * <p>
     * Events are processed sequentially. If a handler or event retrieval fails,
     * the error is logged and processing continues with the next event.
     * </p>
     *
     * @param events the list of events to dispatch; cannot be null but may be empty
     * @throws NullPointerException if events list is null
     */
    public void dispatch(List<DomainEvent> events) {
        Objects.requireNonNull(events, "Events list cannot be null");

        if (events.isEmpty()) {
            appLogger.debug("No events to dispatch");
            return;
        }

        appLogger.debug("Dispatching {} events", events.size());

        for (DomainEvent event : events) {
            dispatchSingleEvent(event);
        }
    }

    /**
     * Returns the total number of registered handlers across all event types.
     *
     * @return the count of all handlers
     */
    public int getHandlerCount() {
        return handlers.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Returns the number of event types that have registered handlers.
     *
     * @return the count of event types
     */
    public int getEventTypeCount() {
        return handlers.size();
    }

    /**
     * Dispatches a single event to all its registered handlers.
     * <p>
     * Logs a warning if no handlers are found for the event type.
     * Errors during handler invocation are caught and logged without stopping other handlers.
     * </p>
     *
     * @param event the event to dispatch; cannot be null
     */
    private void dispatchSingleEvent(DomainEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");

        try {
            List<DomainEventHandler<? extends DomainEvent>> eventHandlers =
                    handlers.getOrDefault(event.getClass(), EMPTY_HANDLER_LIST);

            if (eventHandlers.isEmpty()) {
                appLogger.warn("No handlers found for event type: {}",
                        event.getClass().getSimpleName());
                return;
            }

            appLogger.debug("Processing event {} with {} handlers",
                    event.getClass().getSimpleName(), eventHandlers.size());

            for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
                handleEventWithHandler(event, handler);
            }
        } catch (Exception e) {
            appLogger.error("Unexpected error processing event {}: {}",
                    e, e.getMessage());
        }
    }

    /**
     * Invokes a specific handler with the given event.
     * <p>
     * Any exceptions thrown by the handler are caught, logged, and suppressed
     * to prevent interrupting other handler executions.
     * </p>
     *
     * @param event   the event to handle
     * @param handler the handler to invoke
     */
    @SuppressWarnings("unchecked")
    private void handleEventWithHandler(DomainEvent event, DomainEventHandler<? extends DomainEvent> handler) {
        try {
            ((DomainEventHandler<DomainEvent>) handler).handle(event);
        } catch (Exception e) {
            appLogger.error("Error handling event {} with handler {}: {}",
                    e, event.getClass().getSimpleName(),
                    handler.getClass().getSimpleName(),
                    e.getMessage());
        }
    }
}
