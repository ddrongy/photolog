package photolog.api.dto.travel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class SummaryMapResponse {

    private Long id;
    private String title;
    private String startDate;
    private String endDate;
    private Integer totalDate;
    private List<DayDTO> days;

    public SummaryMapResponse(Travel travel) {
        this.id = travel.getId();
        this.title = travel.getTitle();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.startDate = travel.getStartDate().format(formatter);
        this.endDate = travel.getEndDate().format(formatter);
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
        private String date;
        private List<LocationDTO> locations;

        public DayDTO(Day day) {
            this.id = day.getId();
            this.sequence = day.getSequence();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            this.date = day.getDate().format(formatter);
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
        private String city;

        public LocationDTO(Location location) {
            this.id = location.getId();
            this.name = location.getName();
            this.description = location.getDescription();
            this.photoUrl = location.getPhotos().isEmpty() ? null :
                    location.getPhotos().get(0).getImgUrl();
            this.coordinate = location.getCoordinate();
            this.city = location.getAddress().getCity();
        }
    }
}