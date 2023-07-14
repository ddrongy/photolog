package photolog.api.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Coordinate {
    private Double longitude;
    private Double latitude;

    public Coordinate(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Math.abs(that.latitude - latitude) < 0.001 &&
                Math.abs(that.longitude - longitude) < 0.001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.round(latitude * 1000), Math.round(longitude * 1000));
    }
}
