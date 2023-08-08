package ru.practicum.stats_data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ViewStats {

    private String app;

    private String uri;

    private long hits;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewStats viewStats = (ViewStats) o;
        return Objects.equals(app, viewStats.app) && Objects.equals(uri, viewStats.uri)
                && Objects.equals(hits, viewStats.hits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, uri, hits);
    }
}
