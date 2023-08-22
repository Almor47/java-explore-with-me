package ru.practicum.main_service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main_service.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndUserId(Long eventId, Long userId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);
}
