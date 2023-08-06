package ru.practicum.stats_server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.stats_data.model.ViewStats;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class StatsRepositoryTest {

    @Autowired
    private StatsRepository statsRepository;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void createDataForTest() {
        start = LocalDateTime.of(2020,10,10,10,10,10);
        end = LocalDateTime.of(2025,11,11,11,11,11);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Stats stats1 = Stats.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.2")
                .timestamp(LocalDateTime.parse("2022-09-06 11:00:23",dateTimeFormatter))
                .build();
        statsRepository.save(stats1);

        Stats stats2 = Stats.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.2")
                .timestamp(LocalDateTime.parse("2023-09-06 11:00:23",dateTimeFormatter))
                .build();
        statsRepository.save(stats2);

        Stats stats3 = Stats.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.2")
                .timestamp(LocalDateTime.parse("2023-09-06 11:00:23",dateTimeFormatter))
                .build();
        statsRepository.save(stats3);
    }

    @Test
    void getStatsWithoutUrisAndNotUnique() {
        List<ViewStats> listStats = statsRepository.getStatsBetweenStartAndEnd(start,end);

        assertEquals(2,listStats.size());

        for(ViewStats one:listStats) {
            if(one.getUri().equals("/events/2")) {
                assertEquals(2,one.getHits());
            }
        }
    }

    @Test
    void getStatsWithoutUrisAndUnique() {
        List<ViewStats> listStats = statsRepository.getStatsBetweenStartAndEndUnique(start,end);

        assertEquals(2,listStats.size());

        for(ViewStats one:listStats) {
            if(one.getUri().equals("/events/2")) {
                assertEquals(1,one.getHits());
            }
        }
    }

    @Test
    void getStatsWithUrisAndNotUnique() {
        List<ViewStats> listStats = statsRepository
                .getStatsBetweenStartAndEndAndUris(start,end,List.of("/events/2"));

        assertEquals(1,listStats.size());

        for(ViewStats one:listStats) {
            if(one.getUri().equals("/events/2")) {
                assertEquals(2,one.getHits());
            }
        }

    }

    @Test
    void getStatsWithUrisAndUnique() {
        List<ViewStats> listStats = statsRepository
                .getStatsBetweenStartAndEndAndUrisUnique(start,end,List.of("/events/2"));

        assertEquals(1,listStats.size());

        for(ViewStats one:listStats) {
            if(one.getUri().equals("/events/2")) {
                assertEquals(1,one.getHits());
            }
        }

    }



    @AfterEach
    void clearDataForTest() {
        statsRepository.deleteAll();
    }
}
