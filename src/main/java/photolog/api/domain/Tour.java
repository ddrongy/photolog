package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tour_datas")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;


    private String cat1;
    private String cat2;
    private String cat3;

    @Column(name = "contentid")
    private Long contentId;

    private String firstimage;
    private String firstimage2;

    private String title;

    private String tags;
    private Integer bookmarkCount;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<TourBookmark> tourBookmarks = new ArrayList<>();

    public void setBookmarkCount(Integer count) {
        this.bookmarkCount = count;
    }

}