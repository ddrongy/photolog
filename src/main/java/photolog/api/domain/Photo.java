package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgUrl;

    @Column
    private LocalDateTime dateTime;

    private String description;

    @Embedded
    private Address address;

    @Embedded
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Travel travel;

    //== 연관관계 편의 메서드 ==//
    public void setTravel (Travel travel) {
        this.travel = travel;
        travel.getPhotos().add(this);
    }

    //== 생성 메서드 ==//
    public static Photo createPhoto(Travel travel, String imgUrl, LocalDateTime dateTime, Location location){
        Photo photo = new Photo();
        photo.setTravel(travel);
        photo.setImgUrl(imgUrl);
        photo.setDateTime(dateTime);
        photo.setLocation(location);

        return photo;
    }

    private void setDateTime (LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    private void setLocation (Location location) {
        this.location = location;
    }

    private void setImgUrl (String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
