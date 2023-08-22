package ru.practicum.stats_client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats_data.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        StringBuilder uriBuilder = new StringBuilder("/stats?start={start}&end={end}");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        parameters.put("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (uris != null && uris.size() != 0) {
            for (String one : uris) {
                uriBuilder.append("&uris=").append(one);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }

        return get(uriBuilder.toString(), parameters);
    }

}
