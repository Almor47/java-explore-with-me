package ru.practicum.main_service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation,Long> {
}
