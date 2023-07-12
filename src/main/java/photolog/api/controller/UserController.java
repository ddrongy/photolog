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
import photolog.api.dto.ResponseDto;
import photolog.api.service.UserService;

@Tag(name = "user", description = "유저 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "signup user", description = "유저 회원가입")
    public ResponseEntity<ResponseDto<Long>> register(@RequestBody AddUserRequest request) {
        Long savedId = userService.save(request);
        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User registration successful.");
        response.setData(savedId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "login user", description = "유저 로그인")
    public ResponseEntity<ResponseDto<LoginUserResponse>> login(@RequestBody LoginUserRequest request) {
        LoginUserResponse loginResponse = userService.login(request);
        ResponseDto<LoginUserResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.ok()
                .body(response);
    }

    @DeleteMapping ("/delete/{id}")
    @Operation(summary = "delete user", description = "유저 회원탈퇴")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User deletion successful.");

        return ResponseEntity.ok()
                .body(response);
    }

}
