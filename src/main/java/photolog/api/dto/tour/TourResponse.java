package photolog.api.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.Tour;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TourResponse {
    private Long id;
    private String cat1;
    private String cat2;
    private String cat3;
    private Long contentId;
    private String firstimage;
    private String title;
    private Integer bookmarkCount;

    // 생성자
    public TourResponse(Tour tour) {
        this.id = tour.getId();
        this.cat1 = tour.getCat1();
        this.cat2 = tour.getCat2();
        this.cat3 = tour.getCat3();
        this.contentId = tour.getContentId();
        this.firstimage = tour.getFirstimage();
        this.title = tour.getTitle();
        this.bookmarkCount = tour.getBookmarkCount();
    }

}
