package ru.practicum.main_service.event.model;

import lombok.*;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.event.dto.LocationDto;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    @JoinColumn(name = "event_date")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    private boolean paid;

    @JoinColumn(name = "participant_limit")
    private int participantLimit;

    @JoinColumn(name = "request_moderation")
    private boolean requestModeration;

    @JoinColumn(name = "created_on")
    private LocalDateTime createdOn;

    @JoinColumn(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User initiator;

    private String title;


}
