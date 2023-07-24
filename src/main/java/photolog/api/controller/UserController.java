package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.User.*;
import photolog.api.dto.ResponseDto;
import photolog.api.service.UserService;

@Tag(name = "user", description = "유저 API")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "유저 회원가입")
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
    @Operation(summary = "유저 로그인")
    public ResponseEntity<ResponseDto<LoginUserResponse>> login(@RequestBody LoginUserRequest request) {
        LoginUserResponse loginResponse = userService.login(request);
        ResponseDto<LoginUserResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/nickName/{id}")
    @Operation(summary = "유저 닉네임 변경")
    public ResponseEntity<ResponseDto<NicknameResponse>> changeNickname(@PathVariable Long id,
                                                                        @RequestBody NicknameRequest request) {
        NicknameResponse nicknameResponse = userService.changeNickname(id, request.getNewNickname());
        ResponseDto<NicknameResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User change nickName successful.");
        response.setData(nicknameResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "유저 비밀번호 초기화")
    public ResponseEntity<ResponseDto<Void>> resetPassword(PasswordRequest request) {
        userService.sendTemporaryPassword(request.getPassword());
        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User reset password successful.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "유저 정보 조회")
    public ResponseEntity<ResponseDto<GetUserResponse>> getUser(@PathVariable Long id){
        GetUserResponse findUser = userService.getOneUser(id);

        ResponseDto<GetUserResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User get info successful.");
        response.setData(findUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping ("/delete/{id}")
    @Operation(summary = "유저 회원탈퇴")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("User deletion successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
