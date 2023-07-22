package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.Article;
import photolog.api.domain.Travel;
import photolog.api.domain.User;
import photolog.api.dto.Article.AddArticleResponse;
import photolog.api.dto.User.ArticleResponse;
import photolog.api.repository.ArticleRepository;
import photolog.api.repository.TravelRepository;
import photolog.api.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddArticleResponse save(Long travelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)  // Assuming you have a findByUsername method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        Optional<Travel> travelOpt = travelRepository.findById(travelId);
        if (travelOpt.isPresent()) {
            Travel travel = travelOpt.get();
            Article article = Article.builder()
                    .travel(travel)
                    .user(user)
                    .build();
            articleRepository.save(article);
            return new AddArticleResponse(travel);
        } else {
            // handle case when travelId does not exist in the database
            throw new IllegalArgumentException("Travel not found with id: " + travelId);
        }
    }

    @Transactional
    public List<ArticleResponse> getUserArticle(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("id 존재하지 않음"));

        List<Article> articles = user.getArticles();
        return articles.stream()
                .map(article -> new ArticleResponse(article.getId(), article.getTitle(), article.getContent()))
                .collect(Collectors.toList());
    }
}
