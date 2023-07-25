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
public class TravelInfoResponse {

    private String title;
    private List<DayDTO> days;

    public TravelInfoResponse(Travel travel) {
        this.title = travel.getTitle();
        this.days = travel.getDays().stream()
                .map(DayDTO::new)
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    public static class DayDTO {
        private List<LocationDTO> locations;

        public DayDTO(Day day) {
            this.locations = day.getLocations().stream()
                    .map(LocationDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class LocationDTO {

        private String description;

        public LocationDTO(Location location) {
            this.description = location.getDescription();
        }
    }
}