package ru.practicum.main_service.event.service;

import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.enumerated.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getUserEventsPrivate(Long userId, Integer from, Integer size);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId);

    EventFullDto patchEventByIdPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getUserEventRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchUserEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto patchEventsByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    EventFullDto getEventPublicById(Long id, HttpServletRequest request);

    List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                        Integer from, Integer size, HttpServletRequest request);

}
