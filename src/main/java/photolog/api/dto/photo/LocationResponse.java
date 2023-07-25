package photolog.api.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationResponse {
    private Long locationId;
    private Integer seq;
    private String FullAddress;
    private String locationName;
}
