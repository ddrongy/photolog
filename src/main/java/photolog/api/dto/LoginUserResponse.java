package photolog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginUserResponse {
    private Long userId;
    private String token;
}
