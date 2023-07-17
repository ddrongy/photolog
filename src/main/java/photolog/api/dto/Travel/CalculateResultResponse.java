package photolog.api.dto.Travel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CalculateResultResponse {
    private Integer night;
    private Integer day;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer locationNum;
}
