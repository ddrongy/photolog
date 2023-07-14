package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String description;

    @Embedded
    private Coordinate coordinate;

    @Embedded
    private Address address;

    @Column(name = "location_date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "location")
    private final List<Photo> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Day day;

    public void setDay(Day day) {
        this.day = day;
        day.getLocations().add(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Travel travel;

    public void setTravel (Travel travel) {
        this.travel = travel;
        travel.getLocations().add(this);
    }

    //== 생성 메서드 ==//
    public static Location createLocation(Travel travel, Coordinate coordinate, LocalDate date, Day day, Address address){
        Location location = new Location();
        location.setTravel(travel);
        location.setDay(day);
        location.setCoordinate(coordinate);
        location.setAddress(address);
        location.setDate(date);

        return location;
    }

    private void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    private void setAddress(Address address) {
        this.address = address;
    }

    private void setDate(LocalDate date) {
        this.date = date;
    }
}
