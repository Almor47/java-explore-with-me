package ru.practicum.main_service.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.CompilationMapper;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.compilation.repository.CompilationRepository;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {


    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).toList().stream()
                    .map(compilationMapper::compilationToCompilationDto)
                    .collect(Collectors.toList());
        }
        return compilationRepository.findAllByPinned(pinned, pageable).stream()
                .map(compilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с compId " + compId + " не найдена"));
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {

        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        if (events.size() != newCompilationDto.getEvents().size()) {
            throw new NotFoundException("Найдено меньше событий");
        }
        Compilation compilation = compilationRepository.save(
                compilationMapper.newCompilationDtoToCompilation(newCompilationDto, events));
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с compId " + compId + " не найдена"));
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilationById(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с compId " + compId + " не найдена"));
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            if (events.size() != updateCompilationRequest.getEvents().size()) {
                throw new NotFoundException("Найдено меньше событий");
            }
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }



}
