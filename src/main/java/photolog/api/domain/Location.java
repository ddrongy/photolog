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
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String name;

    private String description; // 개인적인 설명
    private String content; //  게시글 내용

    @Embedded
    private Coordinate coordinate;

    @Embedded
    private Address address;

    @Column(name = "location_date", nullable = false)
    private LocalDate date;

    @Column(name = "seq", nullable = false)
    private Integer sequence;

    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE)
    private final List<Photo> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Day day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Travel travel;

    public void setDay(Day day) {
        this.day = day;
        day.getLocations().add(this);
    }

    public void setTravel (Travel travel) {
        this.travel = travel;
        if (!travel.getLocations().contains(this)) {
            travel.getLocations().add(this);
        }
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateName(String name) {
        this.name = name;
    }

    //== 생성 메서드 ==//
    public static Location createLocation(Travel travel, Coordinate coordinate, LocalDate date, Day day, Address address, Integer sequence){
        Location location = new Location();
        location.setTravel(travel);
        location.setDay(day);
        location.setCoordinate(coordinate);
        location.setAddress(address);
        location.setDate(date);
        location.setSequence(sequence);

        return location;
    }

    private void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    private void setAddress(Address address) {
        this.address = address;
    }
    private void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    private void setDate(LocalDate date) {
        this.date = date;
    }

    public void updateContent(String newContent){
        this.content = newContent;
    }
}
