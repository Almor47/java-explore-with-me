package ru.practicum.main_service.compilation.dto;

import lombok.*;
import ru.practicum.main_service.event.dto.EventShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Long id;

    @NotNull
    private Boolean pinned;

    @NotBlank
    private String title;

    private List<EventShortDto> events;

}
