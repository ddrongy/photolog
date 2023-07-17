package photolog.api.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddUserResponse {
    private Long id;
    private String email;
}
