package ru.practicum.main_service.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {

    private List<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;

}
