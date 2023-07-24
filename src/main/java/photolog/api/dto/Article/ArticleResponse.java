package photolog.api.dto.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.*;
import photolog.api.dto.Travel.SummaryMapResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {
    private Long id;
    private Long travel_id;
    private String title;
//    private LocalDate startDate;
//    private LocalDate endDate;
//    private Integer totalDate;
    private List<DayDTO> days;
    private Integer budget;

    public ArticleResponse(Article article, Travel travel) {
        this.id = article.getId();
        this.travel_id = travel.getId();
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

        private Long id;
        private Integer sequence;
        private LocalDate date;
        private List<LocationDTO> locations;

        public DayDTO(Day day) {
            this.id = day.getId();
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

        private Long id;
        private String name;
        private String content;
        private List<String> photoUrls;
        private Coordinate coordinate;

        public LocationDTO(Location location) {
            this.id = location.getId();
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
