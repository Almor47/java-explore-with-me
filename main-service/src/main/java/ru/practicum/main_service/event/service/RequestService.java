package ru.practicum.main_service.event.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId,Long eventId);

    List<ParticipationRequestDto> getAllUserRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId,Long requestId);
}
