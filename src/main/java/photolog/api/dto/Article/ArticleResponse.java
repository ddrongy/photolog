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
    private List<SummaryMapResponse.DayDTO> days;

    public ArticleResponse(Long id, Travel travel) {
        this.id = id;
        this.travel_id = travel.getId();
        this.title = travel.getTitle();
//        this.startDate = travel.getStartDate();
//        this.endDate = travel.getEndDate();
//        this.totalDate = travel.getTotalDate();
        this.days = travel.getDays().stream()
                .map(SummaryMapResponse.DayDTO::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    public static class DayDTO {

        private Long id;
        private Integer sequence;
        private LocalDate date;
        private List<SummaryMapResponse.LocationDTO> locations;

        public DayDTO(Day day) {
            this.id = day.getId();
            this.sequence = day.getSequence();
            this.date = day.getDate();
            this.locations = day.getLocations().stream()
                    .map(SummaryMapResponse.LocationDTO::new)
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
