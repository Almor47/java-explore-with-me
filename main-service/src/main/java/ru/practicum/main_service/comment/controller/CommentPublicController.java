package ru.practicum.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
@Validated
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentByIdPublic(@PathVariable Long commentId) {
        return commentService.getCommentByIdPublic(commentId);
    }

    @GetMapping("/events/{eventId}")
    public List<CommentDto> getCommentsByEventPublic(@PathVariable Long eventId,
                                                     @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        return commentService.getCommentsByEventPublic(eventId, from, size);
    }
}
