package ru.practicum.main_service.event.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateResult {

    private ParticipationRequestDto confirmedRequests;

    private ParticipationRequestDto rejectedRequests;

}
