package photolog.api.dto.travel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.Theme;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeRequest {
    private List<Theme> theme;
}
