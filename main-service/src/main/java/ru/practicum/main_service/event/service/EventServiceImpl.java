package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.enumerated.StateAction;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Location;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.repository.LocationRepository;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final StatsService statsService;

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Location location = getLocation(newEventDto.getLocation());
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Категория с id " + newEventDto.getCategory() + " не найдена"));
        Event savedEvent = eventRepository.save(eventMapper.NewEventDtoToEvent(newEventDto, location, user, category, LocalDateTime.now(), State.PENDING));
        Map<Long, Long> views = statsService.getViews(List.of(savedEvent));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequest(List.of(savedEvent));
        return eventMapper.EventToEventFullDto(savedEvent, views.getOrDefault(savedEvent.getId(), 0L), confirmedRequests.getOrDefault(savedEvent.getId(), 0L));

    }

    @Override
    public List<EventShortDto> getUserEventsPrivate(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream()
                .map(eventMapper::EventToEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId +
                        " не является инициатором события с eventId " + eventId));
        //return eventMapper.EventToEventFullDto(event);
        return null;
    }

    @Transactional
    @Override
    public EventFullDto patchEventByIdPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        //
        // изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
        // дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
        //
        checkTimeStarts(LocalDateTime.now(), updateEventUserRequest.getEventDate());

        Event event = checkUserAndInitiator(userId, eventId);
        if (event.getState() == State.PUBLISHED) {
            throw new ConflictException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(getLocation(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория  с catId " + updateEventUserRequest.getCategory() + " не найдена")));
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(State.PENDING);
            }
            if (updateEventUserRequest.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(State.CANCELED);
            }
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        //return eventMapper.EventToEventFullDto(eventRepository.save(event));
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequest(Long userId, Long eventId) {
        checkUserAndInitiator(userId, eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::RequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchUserEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = checkUserAndInitiator(userId, eventId);

        return null;
        //
        //
        // если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        //
        //
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size) {
        return null;
    }

    @Override
    @Transactional
    public EventFullDto patchEventsByAdmin(Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        //
        // дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
        //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
        //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        //
        return null;
    }

    @Override
    public EventFullDto getEventPublicById(Long id, HttpServletRequest request) {
        //
        //
        //событие должно быть опубликовано
        //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        //
        //
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id " + id + " не найдено"));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие с id " + id + " id еще не опубликовано");
        }

        statsService.addHit(request);

        return null;
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                               Long from, Long size, HttpServletRequest request) {
        //
        //
        //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
        //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
        //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        //
        //
        return null;
    }

    private Location getLocation(LocationDto locationDto) {
        Location location = LocationMapper.LocationDtoToLocation(locationDto);
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private void checkTimeStarts(LocalDateTime now, LocalDateTime eventDate) {
        if (eventDate == null || now.plusHours(2).isAfter(eventDate)) {
            throw new ConflictException("Остается меньше 2 часов до начала мероприятия ");
        }
    }

    private Event checkUserAndInitiator(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId +
                        " не является инициатором события с eventId " + eventId));
    }

}
