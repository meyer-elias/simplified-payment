package com.eliasmeyer.sp.application.shared;

import com.eliasmeyer.sp.application.shared.logging.AppLogger;
import com.eliasmeyer.sp.domain.shared.DomainEvent;
import com.eliasmeyer.sp.domain.shared.DomainEventDispatcher;
import com.eliasmeyer.sp.domain.shared.DomainEventHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação padrão do dispatcher de eventos de domínio.
 * <p>
 * Gerencia o registro e o despacho de eventos de domínio para seus respectivos handlers. Os
 * handlers são registrados no momento da construção e não podem ser modificados posteriormente. O
 * despacho de eventos é thread-safe e continua mesmo que handlers individuais falhem.
 * </p>
 */
public class DefaultDomainEventDispatcher implements DomainEventDispatcher {

	private static final List<DomainEventHandler<? extends DomainEvent>> EMPTY_HANDLER_LIST = List.of();

	private final AppLogger appLogger;
	private final Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers;

	/**
	 * Cria um novo dispatcher de eventos de domínio com os handlers fornecidos.
	 * <p>
	 * Os handlers são indexados pelo tipo de evento para busca eficiente durante o despacho. Após a
	 * inicialização, o dispatcher não pode ser modificado (registro de handlers imutável).
	 * </p>
	 *
	 * @param appLogger     o logger para registros relacionados ao despacho
	 * @param eventHandlers lista de handlers a registrar; não pode ser nula nem conter elementos
	 *                      nulos
	 * @throws NullPointerException se eventHandlers for nula ou contiver handlers nulos
	 */
	public DefaultDomainEventDispatcher(AppLogger appLogger,
		List<DomainEventHandler<? extends DomainEvent>> eventHandlers) {
		Objects.requireNonNull(eventHandlers, "A lista de handlers não pode ser nula");
		this.appLogger = appLogger;
		this.handlers = new ConcurrentHashMap<>();

		for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
			Objects.requireNonNull(handler, "O handler não pode ser nulo");
			handlers.computeIfAbsent(handler.eventType(), k -> new ArrayList<>()).add(handler);
		}

		// Torna o mapa de handlers imutável após a construção
		handlers.replaceAll((k, v) -> Collections.unmodifiableList(v));
		appLogger.info("DomainEventDispatcher inicializado com {} handlers para {} tipos de evento",
			eventHandlers.size(), handlers.size());
	}

	/**
	 * Despacha uma lista de eventos de domínio para seus handlers registrados.
	 * <p>
	 * Os eventos são processados sequencialmente. Se um handler falhar, o erro é registrado em log
	 * e o processamento continua com o próximo evento.
	 * </p>
	 *
	 * @param events a lista de eventos a despachar; não pode ser nula, mas pode estar vazia
	 * @throws NullPointerException se a lista de eventos for nula
	 */
	@Override
	public void dispatch(List<DomainEvent> events) {
		Objects.requireNonNull(events, "A lista de eventos não pode ser nula");

		if (events.isEmpty()) {
			appLogger.debug("Nenhum evento para despachar");
			return;
		}

		appLogger.debug("Despachando {} eventos", events.size());

		for (DomainEvent event : events) {
			dispatchSingleEvent(event);
		}
	}

	/**
	 * Retorna o número total de handlers registrados em todos os tipos de evento.
	 *
	 * @return a quantidade total de handlers
	 */
	public int getHandlerCount() {
		return handlers.values().stream().mapToInt(List::size).sum();
	}

	/**
	 * Retorna o número de tipos de evento que possuem handlers registrados.
	 *
	 * @return a quantidade de tipos de evento
	 */
	public int getEventTypeCount() {
		return handlers.size();
	}

	/**
	 * Despacha um único evento para todos os seus handlers registrados.
	 * <p>
	 * Registra um aviso em log se nenhum handler for encontrado para o tipo de evento. Erros
	 * durante a invocação dos handlers são capturados e registrados sem interromper os demais
	 * handlers.
	 * </p>
	 *
	 * @param event o evento a despachar; não pode ser nulo
	 */
	private void dispatchSingleEvent(DomainEvent event) {
		List<DomainEventHandler<? extends DomainEvent>> eventHandlers =
			handlers.getOrDefault(event.getClass(), EMPTY_HANDLER_LIST);

		if (eventHandlers.isEmpty()) {
			appLogger.warn("Nenhum handler encontrado para o tipo de evento: {}",
				event.getClass().getSimpleName());
			return;
		}

		appLogger.debug("Processando evento {} com {} handlers",
			event.getClass().getSimpleName(), eventHandlers.size());

		for (DomainEventHandler<? extends DomainEvent> handler : eventHandlers) {
			handleEventWithHandler(event, handler);
		}
	}

	/**
	 * Invoca um handler específico com o evento fornecido.
	 * <p>
	 * Qualquer exceção lançada pelo handler é capturada, registrada em log e suprimida para não
	 * interromper a execução dos demais handlers.
	 * </p>
	 *
	 * @param event   o evento a ser tratado
	 * @param handler o handler a ser invocado
	 */
	@SuppressWarnings("unchecked")
	private void handleEventWithHandler(DomainEvent event,
		DomainEventHandler<? extends DomainEvent> handler) {
		try {
			((DomainEventHandler<DomainEvent>) handler).handle(event);
		} catch (Exception e) {
			appLogger.error("Error handling event {} with handler {}: {}",
				e,
				event.getClass().getSimpleName(),
				handler.getClass().getSimpleName(),
				e.getMessage());
		}
	}
}
