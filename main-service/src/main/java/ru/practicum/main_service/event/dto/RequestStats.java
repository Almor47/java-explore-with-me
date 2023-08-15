package ru.practicum.main_service.event.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestStats {

    private Long eventId;
    private Long confirmedRequests;
}
