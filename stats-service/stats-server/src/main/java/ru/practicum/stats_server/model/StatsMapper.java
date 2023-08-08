package ru.practicum.stats_server.model;

import ru.practicum.stats_data.model.EndpointHit;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper implements Serializable {

    public static Stats endpointHitToStats(EndpointHit endpointHit) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Stats.builder()
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .app(endpointHit.getApp())
                .timestamp(LocalDateTime.parse(endpointHit.getTimestamp(), dateTimeFormatter))
                .build();
    }
}
