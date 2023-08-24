package ru.practicum.main_service.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.CommentMapper;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.repository.CommentRepository;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.exception.BadRequestException;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {


    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с eventId " + eventId + " не найдено"));

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Событие еще не опубликовано");
        }
        Comment comment = commentMapper.newCommentDtoToComment(newCommentDto, user, event,
                LocalDateTime.now());
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto patchCommentById(Long userId, Long commentId, NewCommentDto newCommentDto) {
        checkUser(userId);
        Comment comment = checkUserAndCreator(userId, commentId);
        comment.setText(newCommentDto.getText());
        return commentMapper.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteCommentById(Long userId, Long commentId) {
        checkUserAndCreator(userId, commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto getCommentByIdPrivate(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с  commentId " + commentId + " не найден"));
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getUserCommentsByEventPrivate(Long userId, Long eventId, Integer from, Integer size) {
        checkUser(userId);
        List<Comment> comments = commentRepository.findAllByEventIdAndUserId(eventId, userId, PageRequest.of(from / size, size));
        return comments.stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByIdPublic(Long commentId) {
        return commentMapper.commentToCommentDto(commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с  commentId " + commentId + " не найден")));
    }

    @Override
    public List<CommentDto> getCommentsByEventPublic(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с eventId " + eventId + " не найдено"));
        List<Comment> comments = commentRepository.findAllByEventId(eventId,
                PageRequest.of(from / size, size));
        return comments.stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());
    }

    private void checkUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с userId " + userId + " не найден"));
    }

    private Comment checkUserAndCreator(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("Комментарий с  commentId " + commentId + " не найден"));
        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new BadRequestException("Пользователь с userId " + userId + " не создавал этот комментарий");
        }
        return comment;
    }
}
