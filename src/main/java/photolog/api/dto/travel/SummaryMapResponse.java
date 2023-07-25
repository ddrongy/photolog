package photolog.api.dto.travel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SummaryMapResponse {

    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDate;
    private List<DayDTO> days;

    public SummaryMapResponse(Travel travel) {
        this.id = travel.getId();
        this.title = travel.getTitle();
        this.startDate = travel.getStartDate();
        this.endDate = travel.getEndDate();
        this.totalDate = travel.getTotalDate();
        this.days = travel.getDays().stream()
                .map(DayDTO::new)
                .collect(Collectors.toList());
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
        private String description;
        private String photoUrl;
        private Coordinate coordinate;

        public LocationDTO(Location location) {
            this.id = location.getId();
            this.name = location.getName();
            this.description = location.getDescription();
            this.photoUrl = location.getPhotos().isEmpty() ? null :
                    location.getPhotos().get(0).getImgUrl();
            this.coordinate = location.getCoordinate();
        }
    }
}