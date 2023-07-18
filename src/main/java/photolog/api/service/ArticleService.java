package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import photolog.api.domain.Article;
import photolog.api.domain.Travel;
import photolog.api.dto.Article.AddArticleRequest;
import photolog.api.repository.ArticleRepository;
import photolog.api.repository.TravelRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TravelRepository travelRepository;

    @Transactional
    public Long save(Long travelId, AddArticleRequest request) {
        Optional<Travel> travelOpt = travelRepository.findById(travelId);
        if (travelOpt.isPresent()) {
            Travel travel = travelOpt.get();
            Article article = Article.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .travel(travel)
                    .build();
            articleRepository.save(article);
            return article.getId();
        } else {
            // handle case when travelId does not exist in the database
            throw new IllegalArgumentException("Travel not found with id: " + travelId);
        }
    }
}
