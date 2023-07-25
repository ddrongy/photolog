package photolog.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserResponse {

    private String email;
    private String nickName;
}
