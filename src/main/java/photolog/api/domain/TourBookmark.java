package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tour_bookmarks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TourBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "bookmark_time")
    private LocalDateTime bookmarkTime;


    public TourBookmark(Tour tour, User user, LocalDateTime bookmarkTime) {
        this.tour = tour;
        this.user = user;
        this.bookmarkTime = bookmarkTime;
    }

}