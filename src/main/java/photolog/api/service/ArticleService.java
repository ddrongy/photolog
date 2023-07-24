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
import photolog.api.dto.Article.*;
import photolog.api.repository.ArticleRepository;
import photolog.api.repository.LocationRepository;
import photolog.api.repository.TravelRepository;
import photolog.api.repository.UserRepository;

import java.util.List;
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
            return new ArticleResponse(article, travel);
        } else {
            // handle case when travelId does not exist in the database
            throw new IllegalArgumentException("Travel not found with id: " + travelId);
        }
    }

    @Transactional
    public ArticleResponse updateArticle(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        article.updateTitleAndContent(request.getTitle(), request.getContent());

        Travel travel = article.getTravel();
        List<Location> locations = travel.getLocations();

        if (locations.size() != request.getLocationContent().size()) {
            throw new IllegalArgumentException("The size of the locations and locationContent lists must be the same.");
        }

        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).updateContent(request.getLocationContent().get(i));
        }

        article.setBudget(request.getBudget());

        articleRepository.save(article);
        travelRepository.save(travel);

        return new ArticleResponse(article, travel);
    }

    @Transactional
    public ArticleResponse getArticleById(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        Travel travel = article.getTravel();
        return new ArticleResponse(article, travel);
    }

    @Transactional
    public void delete(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        User user = article.getUser();
        Travel travel = article.getTravel();

        user.getArticles().remove(article);
        userRepository.save(user);  // Save user's changes

        travel.setArticle(null);
        travelRepository.save(travel);  // Save travel's changes

        articleRepository.delete(article);
    }

    public Integer addLike(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        article.addLike();
        articleRepository.save(article);
        return article.getLikes();
    }


    public Integer cancelLike(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        article.cancelLike();
        articleRepository.save(article);
        return article.getLikes();
    }

    public void report(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        article.addReport();
        articleRepository.save(article);
    }

}
