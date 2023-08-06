package ru.practicum.stats_server.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_data.model.ViewStats;
import ru.practicum.stats_server.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
