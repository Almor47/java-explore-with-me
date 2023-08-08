package ru.practicum.stats_server;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_data.model.ViewStats;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.service.StatsServiceImpl;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StatsServiceIntegrationTest {

    private final EntityManager entityManager;

    private final StatsServiceImpl statsService;

    @Test
    void addHit() {

        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.3")
                .timestamp("2021-09-06 11:00:23")
                .build();

        statsService.addHit(endpointHit);

        val query = entityManager
                .createQuery("select s from Stats s where s.uri = :uri", Stats.class);

        Stats stats = query.setParameter("uri",endpointHit.getUri()).getSingleResult();

        assertNotNull(stats.getId());
        assertEquals(endpointHit.getUri(), stats.getUri());
        assertEquals(endpointHit.getApp(), stats.getApp());
        assertEquals(endpointHit.getIp(), stats.getIp());

    }

    @Test
    void getStats() {
        String start = "2020-10-10 10:10:10";
        String end = "2025-10-10 10:10:10";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Stats stats1 = Stats.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.2")
                .timestamp(LocalDateTime.parse("2022-09-06 11:00:23",dateTimeFormatter))
                .build();

        entityManager.persist(stats1);
        entityManager.flush();

        List<ViewStats> actualStats = statsService.getStats(LocalDateTime.parse(start,dateTimeFormatter),
                LocalDateTime.parse(end,dateTimeFormatter),null,false);

        assertEquals(1,actualStats.size());

        assertEquals(stats1.getUri(), actualStats.get(0).getUri());
        assertEquals(stats1.getApp(), actualStats.get(0).getApp());
        assertEquals(1, actualStats.get(0).getHits());

    }
}
