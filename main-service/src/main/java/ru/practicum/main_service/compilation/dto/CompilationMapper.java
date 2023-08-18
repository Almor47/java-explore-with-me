package ru.practicum.main_service.compilation.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.event.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> events);

    CompilationDto compilationToCompilationDto(Compilation compilation);
}
