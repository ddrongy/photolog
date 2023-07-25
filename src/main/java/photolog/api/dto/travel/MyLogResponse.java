package photolog.api.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MyLogResponse {
    private Long travelId;
    private String city;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String thumbnail;
    private Integer photoCnt;
}
