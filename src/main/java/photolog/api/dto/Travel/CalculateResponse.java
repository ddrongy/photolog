package photolog.api.dto.Travel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CalculateResponse {
    private Integer night;
    private Integer day;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer locationNum;
    private Integer photoNum;
    private List<Long> locationList;
}
