package photolog.api.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private Long locationId;
    private Integer seq;
    private String FullAddress;
    private String locationName;
}
