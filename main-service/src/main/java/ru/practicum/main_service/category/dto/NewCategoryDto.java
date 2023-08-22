package ru.practicum.main_service.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {

    @NotBlank(message = "Категория не может иметь пустое название")
    @Size(max = 50)
    private String name;

}
