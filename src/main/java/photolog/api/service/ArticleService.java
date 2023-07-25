package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.*;
import photolog.api.dto.Article.*;
import photolog.api.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleReportRepository articleReportRepository;

    @Transactional
    public ArticleResponse save(Long travelId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        Optional<Travel> travelOpt = travelRepository.findById(travelId);
        if (travelOpt.isPresent()) {
            Travel travel = travelOpt.get();

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

        article.updateTitleAndSummary(request.getTitle(), request.getSummary());

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
        userRepository.save(user);

        travel.setArticle(null);
        travelRepository.save(travel);

        articleRepository.delete(article);
    }

    @Transactional
    public Integer addLike(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article does not exist."));

        if (article.getUser().equals(user)) {
            throw new IllegalArgumentException("본인 게시글에는 좋아요가 불가합니다");
        }

        if (articleLikeRepository.findByArticleAndUser(article, user).isPresent()) {
            throw new IllegalArgumentException("이미 좋아요 한 글 입니다");
        }

        ArticleLike articleLike = new ArticleLike(article, user, LocalDateTime.now());

        article.getLikes().add(articleLike);
        user.getLikes().add(articleLike);

        article.setLikeCount(article.getLikes().size());
        articleRepository.save(article);

        articleLikeRepository.save(articleLike);

        return article.getLikes().size();
    }


    @Transactional
    public Integer cancelLike(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article does not exist."));

        ArticleLike likeToRemove = articleLikeRepository.findByArticleAndUser(article, user)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 하지 않은 글 입니다"));

        article.getLikes().remove(likeToRemove);
        user.getLikes().remove(likeToRemove);

        article.setLikeCount(article.getLikes().size());
        articleRepository.save(article);

        articleLikeRepository.delete(likeToRemove);

        return article.getLikes().size();
    }

    @Transactional
    public Integer addReport(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article does not exist."));

        if (article.getUser().equals(user)) {
            throw new IllegalArgumentException("내가 쓴 글에는 누를 수 없어요");
        }

        if (articleReportRepository.findByArticleAndUser(article, user).isPresent()) {
            throw new IllegalArgumentException("이미 신고한 글이에요");
        }

        ArticleReport articleReport = new ArticleReport(article, user, LocalDateTime.now());

        articleReportRepository.save(articleReport);

        int reportCount = article.getReports().size();
        article.setReportCount(reportCount);
        articleRepository.save(article);

        return reportCount;
    }


    @Transactional
    public List<MyArticleResponse> getMyArticle() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        List<Article> articles = articleRepository.findByUser(user);

        return articles.stream().map(article -> {
            Travel travel = article.getTravel();

            Location firstLocation = travel.getLocations().get(0);
            Photo firstPhoto = firstLocation.getPhotos().get(0);

            return new MyArticleResponse(
                    firstLocation.getAddress().getCity(),
                    article.getTitle(),
                    travel.getStartDate(),
                    travel.getEndDate(),
                    firstPhoto.getImgUrl(),
                    travel.getPhotos().size(),
                    article.getLikes().size()
            );
        }).collect(Collectors.toList());
    }

}
