package ru.practicum.main_service.event.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.event.enumerated.State;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Location;
import ru.practicum.main_service.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", expression = "java(null)")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "publishedOn", expression = "java(null)")
    Event newEventDtoToEvent(NewEventDto newEventDto, Location location, User initiator, Category category, LocalDateTime createdOn, State state);

    EventFullDto eventToEventFullDto(Event event, Long views, Long confirmedRequests);

    EventShortDto eventToEventShortDto(Event event, Long views, Long confirmedRequests);

}
