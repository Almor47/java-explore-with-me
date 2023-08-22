package ru.practicum.stats_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_data.model.ViewStats;
import ru.practicum.stats_server.model.StatsMapper;
import ru.practicum.stats_server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void addHit(EndpointHit endpointHit) {
        statsRepository.save(StatsMapper.endpointHitToStats(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start.isAfter(end)) {
            throw new RuntimeException("Дата старта не может быть после даты окончания");
        }
        if (uris == null) {
            if (!unique) {
                return statsRepository.getStatsBetweenStartAndEnd(start, end);
            } else {
                return statsRepository.getStatsBetweenStartAndEndUnique(start, end);
            }
        }

        if (!unique) {
            return statsRepository.getStatsBetweenStartAndEndAndUris(start, end, uris);
        }
        return statsRepository.getStatsBetweenStartAndEndAndUrisUnique(start, end, uris);

    }
}
