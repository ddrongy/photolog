package photolog.api.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import photolog.api.domain.Article;
import photolog.api.domain.User;
import photolog.api.dto.User.*;
import photolog.api.repository.UserRepository;
import photolog.api.utils.JwtUtil;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.expire}")
    private Long expireTimeMs;

    @Value("${spring.mail.username}")
    private String from;

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

    public GetUserResponse getOneUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("email 존재하지 않음"));

        return new GetUserResponse(user.getEmail(), user.getNickName());
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    public NicknameResponse changeNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("id 존재하지 않음"));

        user.changeNickname(newNickname);

        userRepository.save(user);

        return new NicknameResponse(user.getNickName());
    }

    public void sendTemporaryPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email를 가진 사용자가 존재하지 않습니다."));

        String tempPassword = generateTemporaryPassword(10);
        user.changePassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        MimeMessage mail = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setFrom(from);
            helper.setTo(user.getEmail());
            helper.setSubject("[포토로그] 임시 비밀번호 발급");

            String htmlText = "<html>" +
                    "<head>" +
                    "<style>" +
                    ".container {" +
                    "width: 80%;" +
                    "margin: auto;" +
                    "padding: 20px;" +
                    "border: 1px solid #cccccc;" +
                    "border-radius: 10px;" +
                    "background-color: #f8f8f8;" +
                    "font-family: Arial, sans-serif;" +
                    "text-align: center;" +
                    "}" +
                    ".logo {" +
                    "display: block;" +
                    "margin: auto;" +
                    "width: 20%;" +
                    "}" +
                    ".content {" +
                    "font-size: 18px;" +
                    "}" +
                    ".highlight {" +
                    "font-size: 18px;" +
                    "font-weight: bold;" +
                    "color: #0000ff;" +
                    "}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class=\"container\">" +
                    "<h3>안녕하세요 마이로그 팀입니다</h1>" +
                    "<p class=\"content\"><span class=\"highlight\">" + user.getNickName() + "</span>님이 요청하신 비밀번호 키를 알려드립니다:</p>" +
                    "<p class=\"content\">재발급 비밀번호는 <span class=\"highlight\">" + tempPassword + "</span>입니다.</p>" +
                    "<p class=\"content\">해당 비밀번호로 재 로그인 후 비밀번호 변경해주시길 바랍니다.</p>" +
                    "<p class=\"content\">감사합니다.</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setText(htmlText, true);

            javaMailSender.send(mail);
        } catch (MessagingException e) {
            throw new IllegalStateException("메일 전송에 실패했습니다.");
        }
    }

    private String generateTemporaryPassword(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random rand = new SecureRandom();
        StringBuilder tempPassword = new StringBuilder();

        for (int i = 0; i < length; i++) {
            tempPassword.append(chars.charAt(rand.nextInt(chars.length())));
        }

        return tempPassword.toString();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("email 존재하지 않음"));
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

}
