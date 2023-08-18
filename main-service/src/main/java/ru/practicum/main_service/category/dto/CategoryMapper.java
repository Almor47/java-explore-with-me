package ru.practicum.main_service.category.dto;

import org.mapstruct.Mapper;
import ru.practicum.main_service.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    CategoryDto categoryToCategoryDto(Category category);
}
