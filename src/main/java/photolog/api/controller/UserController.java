package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.AddUserRequest;
import photolog.api.dto.LoginUserRequest;
import photolog.api.dto.LoginUserResponse;
import photolog.api.service.UserService;

@Tag(name = "user", description = "유저 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "signup user", description = "유저 회원가입")
    public ResponseEntity<Long> register(@RequestBody AddUserRequest request) {
        Long savedId = userService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedId);
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "login user", description = "유저 로그인")
    public ResponseEntity<LoginUserResponse> login(@RequestBody LoginUserRequest request) {
        LoginUserResponse response = userService.login(request);

        return ResponseEntity.ok()
                .body(response);
    }

    // 회원 정보 삭제
    @DeleteMapping ("/delete/{id}")
    @Operation(summary = "delete user", description = "유저 회원탈퇴")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok()
                .build();
    }
}
