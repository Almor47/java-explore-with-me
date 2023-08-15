package ru.practicum.main_service.user.service;

import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;

import java.util.List;

public interface UserAdminService {

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto createUser(NewUserRequest user);

    void deleteUserById(Long userId);

}
