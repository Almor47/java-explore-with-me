package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
@Validated
public class EventAdminController {

    private static final String patternDate = "yyyy-MM-dd HH:mm:ss";
    private final EventService eventService;


    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<State> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = patternDate) LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = patternDate) LocalDateTime rangeEnd,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventsByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventService.patchEventsByAdmin(eventId, updateEventAdminRequest);
    }

}
