package ru.practicum.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.main_service.event.enumerated.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private Long event;

    private Long id;

    private Long requester;

    private RequestStatus status;

}
