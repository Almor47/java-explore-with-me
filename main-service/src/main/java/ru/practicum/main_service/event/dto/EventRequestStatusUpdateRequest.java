package ru.practicum.main_service.event.dto;

import lombok.*;
import ru.practicum.main_service.event.enumerated.RequestStatusOnUpdate;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private RequestStatusOnUpdate status;
}
