package photolog.api.dto.Travel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.Theme;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeRequest {
    private Theme theme;
}
