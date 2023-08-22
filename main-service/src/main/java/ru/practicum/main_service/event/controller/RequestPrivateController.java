package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        return requestService.createRequest(userId,eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAllUserRequests(@PathVariable Long userId) {
        return requestService.getAllUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.cancelRequest(userId,requestId);
    }
}
