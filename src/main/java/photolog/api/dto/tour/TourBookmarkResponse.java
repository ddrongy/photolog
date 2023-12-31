package photolog.api.dto.tour;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.Tour;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TourBookmarkResponse {
    private Long tourId;
    private Long contentId;
    private String firstImage;

    public TourBookmarkResponse(Tour tour) {
        this.tourId = tour.getId();
        this.contentId = tour.getContentId();
        this.firstImage = tour.getFirstimage();
    }

}
