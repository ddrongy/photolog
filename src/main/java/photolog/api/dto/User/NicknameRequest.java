package photolog.api.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameRequest {
    private String newNickname;
}
