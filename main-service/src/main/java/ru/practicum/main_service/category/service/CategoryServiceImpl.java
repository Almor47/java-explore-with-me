package ru.practicum.main_service.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.category.dto.CategoryMapper;
import ru.practicum.main_service.category.dto.NewCategoryDto;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category savedCategory = categoryRepository.save(categoryMapper.newCategoryDtoToCategory(newCategoryDto));
        return categoryMapper.categoryToCategoryDto(savedCategory);
    }

    @Override
    public List<CategoryDto> getAllCategory(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(categoryMapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        return categoryMapper.categoryToCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с catId " + catId + " не найдена")));
    }

    @Transactional
    @Override
    public void deleteCategoryById(Long catId) {
        getCategoryById(catId);
        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto patchCategoryById(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new NotFoundException("Категория с catId " + catId + " не найдена"));
        category.setName(categoryDto.getName());

        return categoryMapper.categoryToCategoryDto(categoryRepository.save(category));
    }

}
