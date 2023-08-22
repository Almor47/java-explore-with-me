package ru.practicum.main_service.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    Comment newCommentDtoToComment(NewCommentDto newCommentDto, User user, Event event, LocalDateTime created);

    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    @Mapping(target = "userId", expression = "java(comment.getUser().getId())")
    CommentDto commentToCommentDto(Comment comment);

}
