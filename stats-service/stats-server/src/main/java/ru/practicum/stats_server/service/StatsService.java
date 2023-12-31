package ru.practicum.stats_server.service;

import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_data.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void addHit(EndpointHit endpointHit);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
