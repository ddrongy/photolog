package photolog.api.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String nickName;
    private String password;

}
