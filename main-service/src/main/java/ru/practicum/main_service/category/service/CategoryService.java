package ru.practicum.main_service.category.service;

import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    List<CategoryDto> getAllCategory(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    void deleteCategoryById(Long catId);

    CategoryDto patchCategoryById(Long catId, CategoryDto categoryDto);
}
