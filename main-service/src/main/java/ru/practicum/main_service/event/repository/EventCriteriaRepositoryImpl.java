package ru.practicum.main_service.event.repository;

import ru.practicum.main_service.event.dto.ParametersForAdminSearch;
import ru.practicum.main_service.event.dto.ParametersForPublicSearch;
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

    public List<Event> getEventsPublic(ParametersForPublicSearch parameters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> queryEvents = builder.createQuery(Event.class);
        Root<Event> rootEvents = queryEvents.from(Event.class);
        Predicate predicate = builder.conjunction();

        if (parameters.getText() != null && !parameters.getText().isEmpty()) {
            Predicate predicate1 = builder.like(builder.lower(rootEvents.get("annotation")), "%" + parameters.getText().toLowerCase() + "%");
            Predicate predicate2 = builder.like(builder.lower(rootEvents.get("description")), "%" + parameters.getText().toLowerCase() + "%");
            predicate = builder.and(predicate, builder.or(predicate1, predicate2));
        }

        if (parameters.getCategories() != null && parameters.getCategories().size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("category").in(parameters.getCategories()));
        }

        if (parameters.getPaid() != null) {
            predicate = builder.and(predicate, rootEvents.get("paid").in(parameters.getPaid()));
        }

        if (parameters.getRangeStart() == null && parameters.getRangeEnd() == null) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(rootEvents.get("eventDate"), LocalDateTime.now()));
        } else {
            if (parameters.getRangeStart() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(rootEvents.get("eventDate"), parameters.getRangeStart()));
            }

            if (parameters.getRangeEnd() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(rootEvents.get("eventDate"), parameters.getRangeEnd()));
            }
        }

        predicate = builder.and(predicate, rootEvents.get("state").in(State.PUBLISHED));
        queryEvents.select(rootEvents).where(predicate);
        return entityManager.createQuery(queryEvents)
                .setFirstResult(parameters.getFrom())
                .setMaxResults(parameters.getSize())
                .getResultList();
    }

    public List<Event> getEventsByAdmin(ParametersForAdminSearch parameters) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> queryEvents = builder.createQuery(Event.class);
        Root<Event> rootEvents = queryEvents.from(Event.class);
        Predicate predicate = builder.conjunction();

        if (parameters.getUsers() != null && parameters.getUsers().size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("initiator").in(parameters.getUsers()));
        }

        if (parameters.getStates() != null && parameters.getStates().size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("state").in(parameters.getStates()));
        }

        if (parameters.getCategories() != null && parameters.getCategories().size() != 0) {
            predicate = builder.and(predicate, rootEvents.get("category").in(parameters.getCategories()));
        }

        if (parameters.getRangeStart() != null) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(rootEvents.get("eventDate"), parameters.getRangeStart()));
        }

        if (parameters.getRangeEnd() != null) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(rootEvents.get("eventDate"), parameters.getRangeEnd()));
        }

        queryEvents.select(rootEvents).where(predicate);
        return entityManager.createQuery(queryEvents)
                .setFirstResult(parameters.getFrom())
                .setMaxResults(parameters.getSize())
                .getResultList();
    }
}
