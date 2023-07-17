package photolog.api.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import photolog.api.domain.User;
import photolog.api.dto.User.*;
import photolog.api.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    UserService userService;

    @AfterEach
    void afterEach() {
        // Clean up the database after each test
        userService.deleteAll();
    }

    @Test
    void 회원가입() {
        // given
        Long savedId = userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        // when
        Long result = userService.findByEmail("test@naver.com").getId();

        //then
        Assertions.assertThat(savedId).isEqualTo(result);
    }

    @Test
    void 회원이메일중복(){
        //given
        Long savedId = userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        //when
        IllegalArgumentException returnErrorMessage = assertThrows(IllegalArgumentException.class,
                () -> userService.save(new AddUserRequest("test@naver.com", "test2", "test1234")));

        Assertions.assertThat(returnErrorMessage.getMessage()).isEqualTo("아이디 중복");
    }

    @Test
    void 사용자_정보_조회() {
        // given
        Long savedId = userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        // when
        GetUserResponse user = userService.getOneUser(savedId);

        // then
        Assertions.assertThat(user.getEmail()).isEqualTo("test@naver.com");
        Assertions.assertThat(user.getNickName()).isEqualTo("test");
    }

    @Test
    void 사용자_닉네임_변경() {
        // given
        Long savedId = userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        // when
        NicknameResponse newNickname = userService.changeNickname(savedId, "newNick");

        // then
        Assertions.assertThat(newNickname.getNewNickname()).isEqualTo("newNick");
    }

    @Test
    void 존재하지_않는_사용자_닉네임_변경() {
        // when
        IllegalArgumentException returnErrorMessage = assertThrows(IllegalArgumentException.class, () -> userService.changeNickname(999L, "newNick"));
        // then
        Assertions.assertThat(returnErrorMessage.getMessage()).isEqualTo("email 존재하지 않음");
    }

    @Test
    void 사용자_삭제() {
        // given
        Long savedId = userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        // when
        userService.delete(savedId);

        IllegalArgumentException returnErrorMessage = assertThrows(IllegalArgumentException.class,
                () -> userService.findByEmail("test@naver.com"));

        // then
        Assertions.assertThat(returnErrorMessage.getMessage()).isEqualTo("email 존재하지 않음");
    }

    @Test
    void 로그인_성공() {
        // given
        Long savedId = userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        // when
        LoginUserResponse response = userService.login(new LoginUserRequest("test@naver.com", "test1234"));

        // then
        Assertions.assertThat(response.getUserId()).isEqualTo(savedId);
    }

    @Test
    void 로그인_실패() {
        // given
        userService.save(new AddUserRequest("test@naver.com", "test", "test1234"));

        // when
        IllegalArgumentException wrongPassword = assertThrows(IllegalArgumentException.class, () -> userService.login(new LoginUserRequest("test@naver.com", "wrongpassword")));

        // then
        Assertions.assertThat(wrongPassword.getMessage()).isEqualTo("password 틀림");
    }


}