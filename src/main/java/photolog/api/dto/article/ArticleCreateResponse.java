package photolog.api.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateResponse {
    private Long articleId;
    private Long travelId;
    private String title;
    private List<DayDTO> days;
    private Integer budget;

    public ArticleCreateResponse(Article article, Travel travel) {
        this.articleId = article.getId();
        this.travelId = travel.getId();
        this.title = article.getTitle();
//        this.startDate = travel.getStartDate();
//        this.endDate = travel.getEndDate();
//        this.totalDate = travel.getTotalDate();
        this.days = travel.getDays().stream()
                .map(DayDTO::new)
                .collect(Collectors.toList());
        this.budget = article.getBudget();
    }

    @Getter
    @NoArgsConstructor
    public static class DayDTO {

        private Long dayID;
        private Integer sequence;
        private LocalDate date;
        private List<LocationDTO> locations;

        public DayDTO(Day day) {
            this.dayID = day.getId();
            this.sequence = day.getSequence();
            this.date = day.getDate();
            this.locations = day.getLocations().stream()
                    .map(LocationDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class LocationDTO {

        private Long locationId;
        private String name;
        private String content;
        private List<String> photoUrls;
        private Coordinate coordinate;

        public LocationDTO(Location location) {
            this.locationId = location.getId();
            this.name = location.getName();
            this.content = location.getContent();
            this.photoUrls = location.getPhotos().stream()
                    .filter(photo -> photo.getArticle() == true)
                    .map(Photo::getImgUrl)
                    .collect(Collectors.toList());
            this.coordinate = location.getCoordinate();
        }
    }
}
