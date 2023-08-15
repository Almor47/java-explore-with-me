package ru.practicum.main_service.compilation.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;

import javax.validation.Valid;
import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(Boolean pinned,Long from,Long size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto patchCompilationById(Long compId);
}
