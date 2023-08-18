package ru.practicum.main_service.user.dto;

import org.mapstruct.Mapper;
import ru.practicum.main_service.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User newUserRequestToUser(NewUserRequest newUserRequest);

    UserDto userToUserDto(User user);




}
