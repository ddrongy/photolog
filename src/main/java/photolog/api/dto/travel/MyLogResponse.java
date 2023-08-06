package photolog.api.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MyLogResponse {
    private Long travelId;
    private String city;
    private String title;
    private String startDate;
    private String endDate;
    private String thumbnail;
    private Integer photoCnt;
}
