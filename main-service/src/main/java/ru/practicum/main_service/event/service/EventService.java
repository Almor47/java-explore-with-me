package ru.practicum.main_service.event.service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main_service.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getUserEventsPrivate(Long userId, Integer from, Integer size);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId);

    EventFullDto patchEventByIdPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getUserEventRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult patchUserEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users,List<String> states,List<Long> categories,
                                        LocalDateTime rangeStart,LocalDateTime rangeEnd,Long from,Long size);

    EventFullDto patchEventsByAdmin(Long eventId,UpdateEventUserRequest updateEventUserRequest);
    EventFullDto getEventPublicById(Long id,HttpServletRequest request);

    List<EventShortDto> getEventsPublic(String text,List<Long> categories,Boolean paid,LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,Boolean onlyAvailable,String sort,
                                        Long from,Long size,HttpServletRequest request);

}
