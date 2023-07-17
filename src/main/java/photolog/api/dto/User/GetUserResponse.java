package photolog.api.dto.User;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserResponse {

    private String email;
    private String nickName;
}
