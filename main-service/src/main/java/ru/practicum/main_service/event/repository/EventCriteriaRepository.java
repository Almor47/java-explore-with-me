package ru.practicum.main_service.event.repository;

import ru.practicum.main_service.event.dto.ParametersForAdminSearch;
import ru.practicum.main_service.event.dto.ParametersForPublicSearch;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCriteriaRepository {

    List<Event> getEventsPublic(ParametersForPublicSearch parametersForPublicSearch);

    List<Event> getEventsByAdmin(ParametersForAdminSearch parametersForAdminSearch);
}
