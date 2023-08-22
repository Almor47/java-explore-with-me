package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.dto.RequestMapper;
import ru.practicum.main_service.event.enumerated.RequestStatus;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Request;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final StatsService statsService;


    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId " + eventId + " не найдено"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }

        Optional<Request> savedRequest = requestRepository.findByRequesterIdAndEventId(userId,eventId);

        if (savedRequest.isPresent()) {
            throw new ConflictException("Нельзя добавить повторный запрос");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        RequestStatus requestStatus;
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            requestStatus = RequestStatus.CONFIRMED;
        } else {
            requestStatus = RequestStatus.PENDING;
        }

        Request request = Request.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(requestStatus)
                .build();

        long futureParticipant = statsService.getConfirmedRequest(List.of(event)).getOrDefault(eventId, 0L) + 1;
        long maxParticipant = event.getParticipantLimit();
        if (maxParticipant != 0 && futureParticipant > maxParticipant) {
            throw new ConflictException("Подтвержденных заявок не может быть больше лимита участников");
        }

        return requestMapper.requestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Событие с eventId " + requestId + " не найдено"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Пользователь с userId " + userId
                    + " не является создателем запроса с requestId " + requestId);
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.requestToParticipationRequestDto(requestRepository.save(request));
    }
}
