package photolog.api.dto.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class LocationResponse {
    private Long locationId;
    private Integer sequence;
    private LocalDate date;
    private List<Long> photoIdList;
    private List<String> urlList;
    private String fullAddress;

}
