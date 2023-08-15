package ru.practicum.main_service.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.event.dto.RequestStats;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.RequestRepository;
//import ru.practicum.stats_client.StatsClient;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_data.model.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    //private final StatsClient statsClient;
    //private final ObjectMapper objectMapper;
    //private final RequestRepository requestRepository;

    @Override
    public void addHit(HttpServletRequest request) {
        /*statsClient.addHit(EndpointHit.builder()
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());*/
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        /*ResponseEntity<Object> ans = statsClient.getStats(start, end, uris, unique);
        try {
            return List.of(objectMapper.readValue(objectMapper.writeValueAsString(ans.getBody()), ViewStats[].class));
        } catch (Exception e) {
            throw new ClassCastException(e.getMessage());
        }*/
        return null;
    }

    @Override
    public Map<Long, Long> getViews(List<Event> events) {
        /*Map<Long, Long> views = new HashMap<>();
        if (events.size() == 0) {
            return views;
        }
        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        Optional<LocalDateTime> minTime = publishedEvents.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo);

        if (minTime.isPresent()) {
            LocalDateTime start = minTime.get();
            LocalDateTime end = LocalDateTime.now();
            List<String> uris = publishedEvents.stream()
                    .map(Event::getId)
                    .map(eventId -> "/events/" + eventId)
                    .collect(Collectors.toList());

            List<ViewStats> ans = getStats(start, end, uris, null);
            for (ViewStats one : ans) {
                Long eventId = Long.valueOf(one.getUri().split("/")[2]);
                views.put(eventId, views.getOrDefault(eventId, 0L) + one.getHits());
            }
        }
        return views;*/
        return null;
    }

    @Override
    public Map<Long, Long> getConfirmedRequest(List<Event> events) {
        /*List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        List<Long> eventsId = publishedEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequest = new HashMap<>();
        if (eventsId.size() == 0) {
            return confirmedRequest;
        }
        List<RequestStats> statsRequest = requestRepository.getConfirmedRequest(eventsId);
        for (RequestStats one : statsRequest) {
            confirmedRequest.put(one.getEventId(), one.getConfirmedRequests());
        }
        return confirmedRequest;*/
        return null;
    }
}
