package ru.practicum.main_service.event.dto;

import ru.practicum.main_service.event.model.Location;

import java.io.Serializable;


public class LocationMapper implements Serializable {

    public static Location LocationDtoToLocation(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }
}
