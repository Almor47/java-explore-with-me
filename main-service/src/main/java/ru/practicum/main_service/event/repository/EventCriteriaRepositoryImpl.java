package ru.practicum.main_service.event.repository;

import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

public class EventCriteriaRepositoryImpl implements EventCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> getEventsPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> queryEvents = builder.createQuery(Event.class);
        Root<Event> rootEvents = queryEvents.from(Event.class);
        Predicate predicate = builder.conjunction();

        if (text != null && !text.isEmpty()) {
            Predicate predicate1 = builder.like(builder.lower(rootEvents.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate predicate2 = builder.like(builder.lower(rootEvents.get("description")), "%" + text.toLowerCase() + "%");
            predicate = builder.and(predicate, builder.or(predicate1, predicate2));
        }

        if (categories != null && categories.size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("category").in(categories));
        }

        if (paid != null) {
            predicate = builder.and(predicate, rootEvents.get("paid").in(paid));
        }

        if (rangeStart == null && rangeEnd == null) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(rootEvents.get("eventDate"), LocalDateTime.now()));
        } else {
            if (rangeStart != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(rootEvents.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(rootEvents.get("eventDate"), rangeEnd));
            }
        }

        predicate = builder.and(predicate, rootEvents.get("state").in(State.PUBLISHED));
        queryEvents.select(rootEvents).where(predicate);
        return entityManager.createQuery(queryEvents)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Event> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> queryEvents = builder.createQuery(Event.class);
        Root<Event> rootEvents = queryEvents.from(Event.class);
        Predicate predicate = builder.conjunction();

        if (users != null && users.size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("initiator").in(users));
        }

        if (states != null && states.size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("state").in(states));
        }

        if (categories != null && categories.size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("category").in(categories));
        }

        if (rangeStart != null) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(rootEvents.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(rootEvents.get("eventDate"), rangeEnd));
        }

        queryEvents.select(rootEvents).where(predicate);
        return entityManager.createQuery(queryEvents)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
    }
}
