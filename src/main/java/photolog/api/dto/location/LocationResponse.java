package photolog.api.dto.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LocationResponse {
    private Long locationId;
    private Integer sequence;
    private String date;
    private List<Long> photoIdList;
    private List<String> urlList;
    private String fullAddress;
    private String name;
    private String description;
}
