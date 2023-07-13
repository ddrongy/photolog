package photolog.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import photolog.api.domain.User;
import photolog.api.dto.AddUserRequest;
import photolog.api.dto.LoginUserRequest;
import photolog.api.dto.LoginUserResponse;
import photolog.api.repository.UserRepository;
import photolog.api.utils.JwtUtil;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.expire}")
    private Long expireTimeMs;

    public Long save(AddUserRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("아이디 중복");
                });
        return userRepository.save(User.builder()
                .email(request.getEmail())
                .nickName(request.getNickName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build()).getId();

    }

    public LoginUserResponse login(LoginUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("email 존재하지 않음"));
        // password 틀림
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("password 틀림");
        }
        String token = JwtUtil.createJwt(user.getUsername(), secretKey, expireTimeMs);

        return new LoginUserResponse(user.getId(), "Bearer " + token);
    }
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
