package ru.practicum.main_service.event.repository;

import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCriteriaRepository {

    List<Event> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                LocalDateTime rangeEnd, Integer from, Integer size);

    List<Event> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
