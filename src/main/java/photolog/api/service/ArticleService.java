package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.Article;
import photolog.api.domain.Location;
import photolog.api.domain.Travel;
import photolog.api.domain.User;
import photolog.api.dto.Article.ArticleResponse;
import photolog.api.dto.Article.LocationContentRequest;
import photolog.api.dto.Article.TitleRequest;
import photolog.api.dto.User.UserArticleResponse;
import photolog.api.repository.ArticleRepository;
import photolog.api.repository.LocationRepository;
import photolog.api.repository.TravelRepository;
import photolog.api.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public ArticleResponse save(Long travelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)  // Assuming you have a findByUsername method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        Optional<Travel> travelOpt = travelRepository.findById(travelId);
        if (travelOpt.isPresent()) {
            Travel travel = travelOpt.get();

            // Check if the travel already has an article
            if (travel.getArticle() != null) {
                throw new IllegalArgumentException("Travel already has an associated article.");
            }

            Article article = Article.builder()
                    .travel(travel)
                    .user(user)
                    .build();
            articleRepository.save(article);
            return new ArticleResponse(travel);
        } else {
            // handle case when travelId does not exist in the database
            throw new IllegalArgumentException("Travel not found with id: " + travelId);
        }
    }

    @Transactional
    public void updateContent(Long locationId, LocationContentRequest request){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("location 존재하지 않음"));

        location.updateContent(request.getContent());
        locationRepository.save(location);
    }

    @Transactional
    public void updateTitle(Long articleId, TitleRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        article.updateTitle(request.getTitle());
        articleRepository.save(article);
    }

    public ArticleResponse getArticleById(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        Travel travel = article.getTravel();
        return new ArticleResponse(travel);
    }

    @Transactional
    public void delete(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        User user = article.getUser();
        Travel travel = article.getTravel();

        user.getArticles().remove(article);
        travel.setArticle(null);

        articleRepository.delete(article);
    }


}
