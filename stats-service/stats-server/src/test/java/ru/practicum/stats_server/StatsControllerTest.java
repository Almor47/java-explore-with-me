package ru.practicum.stats_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats_data.model.EndpointHit;
import ru.practicum.stats_server.controller.StatsController;
import ru.practicum.stats_server.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
public class StatsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;


    @Test
    @SneakyThrows
    void addHit_whenValid_thenReturnOk() {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.3")
                .timestamp("2021-09-06 11:00:23")
                .build();

        mvc.perform(post("/hit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(endpointHit)))
                .andExpect(status().isOk());


        verify(statsService, times(1)).addHit(endpointHit);
    }

    @Test
    @SneakyThrows
    void addHitN_whenNotValid_thenReturnBadRequest() {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("ewm-main-service")
                .uri(null)
                .ip("192.163.0.3")
                .timestamp("2021-09-06 11:00:23")
                .build();

        mvc.perform(post("/hit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(endpointHit)))
                .andExpect(status().isBadRequest());

        verify(statsService, never()).addHit(endpointHit);
    }

    @Test
    @SneakyThrows
    void getStats_whenValid_ThenReturn() {

        String start = "2000-10-10 10:10:10";
        String end = "2010-10-10 10:10:10";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        mvc.perform(get("/stats?" + "start={start}&" + "end={end}", start, end))
                .andExpect(status().isOk());

        verify(statsService, times(1))
                .getStats(LocalDateTime.parse(start, dateTimeFormatter),
                        LocalDateTime.parse(end, dateTimeFormatter), null, false);

    }


}
