package ru.practicum.main_service.event.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float lat;

    private float lon;

}
