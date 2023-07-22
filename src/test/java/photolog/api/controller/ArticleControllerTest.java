package photolog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import photolog.api.domain.Article;
import photolog.api.domain.Travel;
import photolog.api.domain.User;
import photolog.api.repository.ArticleRepository;
import photolog.api.repository.TravelRepository;
import photolog.api.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TravelRepository travelRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        articleRepository.deleteAll();
    }

    @DisplayName("addArticle: 게시글 추가에 성공한다")
    @Test
    public void addArticle() throws Exception {
        //given
        //user, travel 기본생성
        User user = userRepository.save(User.builder()
                .email("test@naver.com")
                .nickName("test")
                .password("test1234")
                .build());
        Travel travel = new Travel(user);
        travelRepository.save(travel);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("test@naver.com", "password");

        // SecurityContext 에 인증토큰 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String url ="/api/articles/"+travel.getId();
        final String title = "title";
        final String content = "content";
//        AddArticleRequest userRequest = new AddArticleRequest(title, content);
//
//        final String requestBody = objectMapper.writeValueAsString(userRequest);
//
//        //when
//        ResultActions result = mockMvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .content(requestBody)
//        );
//
//        //then
//        result.andExpect(status().isCreated());
//
//        List<Article> articles = articleRepository.findAll();
//
//        assertThat(articles.size()).isEqualTo(1);
//        assertThat(articles.get(0).getTitle()).isEqualTo(title);
//        assertThat(articles.get(0).getContent()).isEqualTo(content);
//        assertThat(articles.get(0).getTravel().getId()).isEqualTo(travel.getId());
//        assertThat(articles.get(0).getUser().getId()).isEqualTo(user.getId());

    }
}