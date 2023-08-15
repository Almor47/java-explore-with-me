package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
@Validated
public class EventPublicController {

    private static final String patternDate = "yyyy-MM-dd HH:mm:ss";
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@RequestParam String text,
                                               @RequestParam List<Long> categories,
                                               @RequestParam Boolean paid,
                                               @RequestParam @DateTimeFormat(pattern = "patternDate") LocalDateTime rangeStart,
                                               @RequestParam @DateTimeFormat(pattern = "patternDate") LocalDateTime rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam String sort,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Long from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive Long size,
                                               HttpServletRequest request) {
        return eventService.getEventsPublic(text,categories,paid,rangeStart,rangeEnd,onlyAvailable,sort,from,size,request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventPublicById(@PathVariable Long id,
                                           HttpServletRequest request) {
        return eventService.getEventPublicById(id,request);
    }

}
