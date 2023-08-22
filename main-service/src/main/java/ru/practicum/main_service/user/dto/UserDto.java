package ru.practicum.main_service.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String email;

    private Long id;

    private String name;

}
