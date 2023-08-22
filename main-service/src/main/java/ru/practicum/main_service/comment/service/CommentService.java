package ru.practicum.main_service.comment.service;

import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto patchCommentById(Long userId, Long commentId, NewCommentDto newCommentDto);

    void deleteCommentById(Long userId, Long commentId);

    CommentDto getCommentByIdPrivate(Long userId, Long commentId);

    List<CommentDto> getUserCommentsByEventPrivate(Long userId, Long eventId, Integer from, Integer size);

    CommentDto getCommentByIdPublic(Long commentId);

    List<CommentDto> getCommentsByEventPublic(Long eventId, Integer from, Integer size);
}
