package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Embedded
    private Coordinate coordinate;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Travel travel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Location location;

    //== 연관관계 편의 메서드 ==//
    public void setTravel (Travel travel) {
        this.travel = travel;
        travel.getPhotos().add(this);
    }
    public void setLocation (Location location) {
        this.location = location;
        location.getPhotos().add(this);
    }

    public void delTravel () {
        this.travel.getPhotos().remove(this);
        this.travel = null;
    }
    public void delLocation () {
        this.location.getPhotos().remove(this);
        this.location = null;
    }



    public void changeLocation(Location newLocation) {
        if (this.location != null) {
            this.location.removePhoto(this);
        }
        this.location = newLocation;
    }

    //== 생성 메서드 ==//
    public static Photo createPhoto(Travel travel, String imgUrl, LocalDateTime dateTime, Coordinate coordinate, Address address){
        Photo photo = new Photo();
        photo.setTravel(travel);
        photo.setImgUrl(imgUrl);
        photo.setDateTime(dateTime);
        photo.setCoordinate(coordinate);
        photo.setAddress(address);

        return photo;
    }

    private void setDateTime (LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    private void setCoordinate (Coordinate location) {
        this.coordinate = location;
    }

    private void setAddress (Address address) {
        this.address = address;
    }

    private void setImgUrl (String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
