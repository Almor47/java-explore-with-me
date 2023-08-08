package ru.practicum.stats_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats_data.model.ViewStats;
import ru.practicum.stats_server.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("select new ru.practicum.stats_data.model.ViewStats(s.app, s.uri, count(s.uri)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri " +
            "order by count(s.uri) desc ")
    List<ViewStats> getStatsBetweenStartAndEnd(LocalDateTime start, LocalDateTime end);


    @Query("select new ru.practicum.stats_data.model.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc ")
    List<ViewStats> getStatsBetweenStartAndEndUnique(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats_data.model.ViewStats(s.app, s.uri, count(s.uri)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 and s.uri in (?3) " +
            "group by s.app, s.uri " +
            "order by count(s.uri) desc ")
    List<ViewStats> getStatsBetweenStartAndEndAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);


    @Query("select new ru.practicum.stats_data.model.ViewStats(s.app, s.uri, count(distinct s.ip)) " +
            "from Stats s " +
            "where s.timestamp between ?1 and ?2 and s.uri in(?3) " +
            "group by s.app, s.uri " +
            "order by count(distinct s.ip) desc ")
    List<ViewStats> getStatsBetweenStartAndEndAndUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
