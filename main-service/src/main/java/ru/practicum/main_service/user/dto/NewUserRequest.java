package ru.practicum.main_service.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @Email(message = "У пользователя невалидный email")
    @NotBlank(message = "У пользователя не может быть пустой email")
    @Size(min = 6, max = 254)
    private String email;

    @NotBlank(message = "У пользователя не может быть пустое имя")
    @Size(min = 2, max = 250)
    private String name;

}
