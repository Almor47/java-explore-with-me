package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.enumerated.RequestStatus;
import ru.practicum.main_service.event.enumerated.RequestStatusOnUpdate;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.enumerated.StateAction;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Location;
import ru.practicum.main_service.event.model.Request;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.repository.LocationRepository;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.main_service.exception.BadRequestException;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
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
        checkTimeStarts(LocalDateTime.now(), newEventDto.getEventDate());
        Event savedEvent = eventRepository.save(eventMapper.newEventDtoToEvent(newEventDto, location, user, category, LocalDateTime.now(), State.PENDING));
        return getEventFullDto(savedEvent);

    }

    @Override
    public List<EventShortDto> getUserEventsPrivate(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequest(events);
        return events.stream()
                .map(event -> eventMapper.eventToEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId +
                        " не является инициатором события с eventId " + eventId));
        return getEventFullDto(event);
    }


    @Transactional
    @Override
    public EventFullDto patchEventByIdPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

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

        Event savedEvent = eventRepository.save(event);
        return getEventFullDto(savedEvent);

    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequest(Long userId, Long eventId) {
        checkUserAndInitiator(userId, eventId);
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult patchUserEventRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = checkUserAndInitiator(userId, eventId);

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration() || eventRequestStatusUpdateRequest.getRequestIds().size() == 0) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        boolean flag = requests.stream()
                .map(Request::getStatus)
                .allMatch(status -> status.equals(RequestStatus.PENDING));

        if (!flag) {
            throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusOnUpdate.CONFIRMED)) {
            long futureParticipant = statsService.getConfirmedRequest(List.of(event)).getOrDefault(eventId, 0L) + eventRequestStatusUpdateRequest.getRequestIds().size();
            long maxParticipant = event.getParticipantLimit();
            if (maxParticipant != 0 && futureParticipant > maxParticipant) {
                throw new ConflictException("Подтвержденных заявок не может быть больше лимита участников");
            }
            for (Request one : requests) {
                one.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(one);
            }
        } else if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusOnUpdate.REJECTED)) {
            for (Request one : requests) {
                one.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(one);
            }
        }


        return new EventRequestStatusUpdateResult(confirmedRequests.stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList()), rejectedRequests.stream()
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList()));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConflictException("Время старта не может быть позже времени окончания");
        }
        ParametersForAdminSearch parametersForAdminSearch = ParametersForAdminSearch.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        List<Event> events = eventRepository.getEventsByAdmin(parametersForAdminSearch);
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequest(events);
        return events.stream()
                .map(event -> eventMapper.eventToEventFullDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto patchEventsByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        checkTimeStarts(LocalDateTime.now().minusHours(1L), updateEventAdminRequest.getEventDate());

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с eventId " + eventId + " не найдено"));

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getLocation(updateEventAdminRequest.getLocation()));
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория  с catId " + updateEventAdminRequest.getCategory() + " не найдена")));
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!(event.getState() == State.PENDING)) {
                throw new ConflictException("Cобытие можно публиковать, только если оно в состоянии ожидания публикации");
            }

            if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) {
                event.setState(State.REJECTED);
            }
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        Event savedEvent = eventRepository.save(event);
        return getEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventPublicById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id " + id + " не найдено"));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие с id " + id + " id еще не опубликовано");
        }
        statsService.addHit(request);
        return getEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd, Boolean onlyAvailable, String sort,
                                               Integer from, Integer size, HttpServletRequest request) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Время старта позже времени окончания события ");
        }
        ParametersForPublicSearch parametersForPublicSearch = ParametersForPublicSearch.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        List<Event> events = eventRepository.getEventsPublic(parametersForPublicSearch);

        if (events.size() == 0) {
            return List.of();
        }
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequest(events);
        Map<Long, Integer> participantsLimit = new HashMap<>();
        for (Event one : events) {
            participantsLimit.put(one.getId(), one.getParticipantLimit());
        }
        List<EventShortDto> eventsShortDto = events.stream()
                .map(event -> eventMapper.eventToEventShortDto(event,
                        views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        if (onlyAvailable) {
            eventsShortDto = eventsShortDto.stream()
                    .filter(event -> participantsLimit.get(event.getId()) == 0 || participantsLimit.get(event.getId()) > event.getConfirmedRequests())
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals("VIEWS")) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
        }
        if (sort != null && sort.equals("EVENT_DATE")) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }
        statsService.addHit(request);
        return eventsShortDto;
    }

    private Location getLocation(LocationDto locationDto) {
        Location location = LocationMapper.locationDtoToLocation(locationDto);
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(location));
    }

    private void checkTimeStarts(LocalDateTime now, LocalDateTime eventDate) {
        if (eventDate != null && now.plusHours(2).isAfter(eventDate)) {
            throw new BadRequestException("Остается меньше 2 часов до начала мероприятия ");
        }
    }

    private Event checkUserAndInitiator(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Пользователь с userId " + userId + " не найден"));
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с userId " + userId +
                        " не является инициатором события с eventId " + eventId));
    }

    private EventFullDto getEventFullDto(Event savedEvent) {
        Map<Long, Long> views = statsService.getViews(List.of(savedEvent));
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequest(List.of(savedEvent));
        return eventMapper.eventToEventFullDto(savedEvent,
                views.getOrDefault(savedEvent.getId(), 0L),
                confirmedRequests.getOrDefault(savedEvent.getId(), 0L));

    }

}
