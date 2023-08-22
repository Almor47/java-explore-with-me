package ru.practicum.stats_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_data.model.ViewStats;
import ru.practicum.stats_server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated

public class StatsController {

    private final StatsService statsService;
    private static final String patternDate = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody EndpointHit endpointHit) {
        log.info("Create hit {}", endpointHit);
        statsService.addHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = patternDate) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = patternDate) LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Create query with start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsService.getStats(start, end, uris, unique);
    }


}
