package ru.practicum.main_service.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private List<Long> events;

    @NotNull
    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
