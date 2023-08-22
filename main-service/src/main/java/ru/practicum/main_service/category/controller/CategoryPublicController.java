package ru.practicum.main_service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Slf4j
@Validated
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategory(@RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return categoryService.getAllCategory(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
