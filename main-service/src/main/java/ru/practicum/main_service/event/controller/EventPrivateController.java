package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
@Validated
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addEvent(userId,newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getUserEventsPrivate(@PathVariable Long userId,
                                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getUserEventsPrivate(userId,from,size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdPrivate(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        return eventService.getEventByIdPrivate(userId,eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventByIdPrivate(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.patchEventByIdPrivate(userId,eventId,updateEventUserRequest);
    }

    @GetMapping("{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequest(@PathVariable Long userId,
                                                             @PathVariable Long eventId) {
        return eventService.getUserEventRequest(userId,eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult patchUserEventRequest(@PathVariable Long userId,
                                                                @PathVariable Long eventId,
                                                                @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.patchUserEventRequest(userId,eventId,eventRequestStatusUpdateRequest);
    }
}

