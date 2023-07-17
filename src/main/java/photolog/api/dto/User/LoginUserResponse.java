package photolog.api.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginUserResponse {
    private Long userId;
    private String token;
}
