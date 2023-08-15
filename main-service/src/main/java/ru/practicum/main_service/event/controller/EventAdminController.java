package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.UpdateEventUserRequest;
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
    public List<EventFullDto> getEventsByAdmin(@RequestParam List<Long> users,
                                               @RequestParam List<String> states,
                                               @RequestParam List<Long> categories,
                                               @RequestParam @DateTimeFormat(pattern = patternDate) LocalDateTime rangeStart,
                                               @RequestParam @DateTimeFormat(pattern = patternDate) LocalDateTime rangeEnd,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive Long size) {
        return eventService.getEventsByAdmin(users,states,categories,rangeStart,rangeEnd,from,size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventsByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.patchEventsByAdmin(eventId,updateEventUserRequest);
    }

}
