package ru.practicum.stats_server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.repository.StatsRepository;
import ru.practicum.stats_server.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {

    @Mock
    private StatsRepository statsRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    void addHit() {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.3")
                .timestamp("2021-09-06 11:00:23")
                .build();

        statsService.addHit(endpointHit);

        verify(statsRepository, times(1)).save(any(Stats.class));
    }

    @Test
    void getStatsWithoutUrisAndNotUnique() {

        statsService.getStats(LocalDateTime.now(), LocalDateTime.now(), null, false);

        verify(statsRepository, times(1))
                .getStatsBetweenStartAndEnd(any(LocalDateTime.class), any(LocalDateTime.class));

    }

    @Test
    void getStatsWithoutUrisAndUnique() {

        statsService.getStats(LocalDateTime.now(), LocalDateTime.now(), null, true);

        verify(statsRepository, times(1))
                .getStatsBetweenStartAndEndUnique(any(LocalDateTime.class), any(LocalDateTime.class));

    }

    @Test
    void getStatsWithUrisAndNotUnique() {

        statsService.getStats(LocalDateTime.now(), LocalDateTime.now(), List.of(""), false);

        verify(statsRepository, times(1))
                .getStatsBetweenStartAndEndAndUris(any(LocalDateTime.class),
                        any(LocalDateTime.class),any());

    }

    @Test
    void getStatsWithUrisAndUnique() {

        statsService.getStats(LocalDateTime.now(), LocalDateTime.now(), List.of(""), true);

        verify(statsRepository, times(1))
                .getStatsBetweenStartAndEndAndUrisUnique(any(LocalDateTime.class),
                        any(LocalDateTime.class), any());

    }


}
