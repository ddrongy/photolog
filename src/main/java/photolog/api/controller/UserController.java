package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import photolog.api.config.jwt.TokenProvider;
import photolog.api.domain.User;
import photolog.api.dto.AddUserRequest;
import photolog.api.dto.LoginUserRequest;
import photolog.api.repository.UserRepository;
import photolog.api.service.UserService;

import java.time.Duration;

@Tag(name = "user", description = "유저 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
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
    public ResponseEntity<String> login(@RequestBody LoginUserRequest request) {
        String token = userService.login(request);

        return ResponseEntity.ok()
                .body(token);
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
