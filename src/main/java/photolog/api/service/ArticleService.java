package photolog.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import photolog.api.domain.*;
import photolog.api.dto.article.*;
import photolog.api.repository.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleReportRepository articleReportRepository;
    private final ArticleBookmarkRepository articleBookmarkRepository;
    private final LocationRepository locationRepository;

    public Specification<Article> createSpec(List<Theme> themes, String degree, String city, Integer startBudget, Integer endBudget, Integer day) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            System.out.println("city = " + city + " degree = " + degree);
            if(themes != null && !themes.isEmpty()) {
                List<Predicate> themePredicates = new ArrayList<>();
                for (Theme theme : themes) {
                    themePredicates.add(cb.isMember(theme, root.join("travel").get("theme")));
                }
                predicates.add(cb.or(themePredicates.toArray(new Predicate[0])));
            }
            if(degree != null) {
                Subquery<Location> locationSubQuery = query.subquery(Location.class);
                Root<Article> articleRoot = locationSubQuery.correlate(root);
                Join<Article, Travel> travelJoin = articleRoot.join("travel");
                ListJoin<Travel, Location> locationsJoin = travelJoin.joinList("locations");
                locationSubQuery
                        .select(locationsJoin)
                        .where(cb.like(locationsJoin.get("address").get("degree"), "%" + degree + "%"));

                predicates.add(cb.exists(locationSubQuery));
            }
            if(city != null) {
                Subquery<Location> locationSubQuery = query.subquery(Location.class);
                Root<Article> articleRoot = locationSubQuery.correlate(root);
                Join<Article, Travel> travelJoin = articleRoot.join("travel");
                ListJoin<Travel, Location> locationsJoin = travelJoin.joinList("locations");
                locationSubQuery
                        .select(locationsJoin)
                        .where(cb.like(locationsJoin.get("address").get("city"), "%" + city + "%"));

                predicates.add(cb.exists(locationSubQuery));
            }
            if(startBudget != null && endBudget != null) {
                predicates.add(cb.between(root.get("budget"), startBudget, endBudget));
            } else if(startBudget != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("budget"), startBudget));
            } else if(endBudget != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("budget"), endBudget));
            }
            if(day != null) {
                predicates.add(cb.equal(root.join("travel").get("totalDate"), day));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    @Transactional
    public Long save(Long travelId, ArticleCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("travel 존재하지 않음"));
        List<Location> locations = travel.getLocations();

        if (travel.getArticle() != null) {
            throw new IllegalArgumentException("이미 해당 여행에 생성된 게시글이 존재 합니다.");
        }

        Article article = Article.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .budget(request.getBudget())
                .member(request.getMember())
                .travel(travel)
                .user(user)
                .build();
        article = articleRepository.save(article);
        travelRepository.save(travel);

        if (locations.size() != request.getLocationContent().size()) {
            throw new IllegalArgumentException("article의 Location 수와 실제 location수가 일치하지 않음");
        }
        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).updateContent(request.getLocationContent().get(i));
        }
        System.out.println("travel.article = " + travel.getArticle().getId());
        return article.getId();

    }

    @Transactional
    public ArticleDetailResponse updateArticle(Long articleId, ArticleUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        article.updateTitleAndSummary(request.getTitle(), request.getSummary());

        Travel travel = article.getTravel();
        List<Location> locations = travel.getLocations();

        if (locations.size() != request.getLocationContent().size()) {
            throw new IllegalArgumentException("article의 Location 수와 실제 location수가 일치하지 않음");
        }

        for (int i = 0; i < locations.size(); i++) {
            locations.get(i).updateContent(request.getLocationContent().get(i));
        }

        article.setBudget(request.getBudget());
        article.setMember(request.getMember());

        articleRepository.save(article);
        travelRepository.save(travel);
        Boolean like=false, bookmark=false;
        if (articleLikeRepository.findByArticleAndUser(article, user).isPresent()) {
            like=true;
        }
        if (articleBookmarkRepository.findByArticleAndUser(article, user).isPresent()) {
            bookmark=true;
        }

        return new ArticleDetailResponse(article, travel, like, bookmark);
    }

    @Transactional
    public TravelInfoResponse getTravelInfo(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        Travel travel = article.getTravel();

        return new TravelInfoResponse(travel);
    }

    @Transactional
    public ArticleDetailResponse getArticleById(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("article 존재하지 않음"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));


        if(article.getHide()){
            throw new IllegalArgumentException("광고/홍보글 신고로 인해 숨김 처리된 글입니다");
        }

        Travel travel = article.getTravel();
        Boolean like=false, bookmark=false;
        if (articleLikeRepository.findByArticleAndUser(article, user).isPresent()) {
            like=true;
        }
        if (articleBookmarkRepository.findByArticleAndUser(article, user).isPresent()) {
            bookmark=true;
        }

        return new ArticleDetailResponse(article, travel, like, bookmark);
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
        articleLikeRepository.save(articleLike);

        return article.getLikeCount();
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
        articleLikeRepository.delete(likeToRemove);

        return article.getLikeCount();
    }

    @Transactional
    public Integer addBookmark(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article does not exist."));

        if (article.getUser().equals(user)) {
            throw new IllegalArgumentException("본인 게시글에는 북마크가 불가합니다");
        }

        if (articleBookmarkRepository.findByArticleAndUser(article, user).isPresent()) {
            throw new IllegalArgumentException("이미 북마크 한 글 입니다");
        }

        ArticleBookmark articleBookmark = new ArticleBookmark(article, user, LocalDateTime.now());
        article.getArticleBookmarks().add(articleBookmark);
        user.getArticleBookmarks().add(articleBookmark);

        article.setBookmarkCount(article.getArticleBookmarks().size());
        articleBookmarkRepository.save(articleBookmark);

        return article.getBookmarkCount();
    }


    @Transactional
    public Integer cancelBookmark(Long articleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article does not exist."));

        ArticleBookmark articleBookmark = articleBookmarkRepository.findByArticleAndUser(article, user)
                .orElseThrow(() -> new IllegalArgumentException("북마크 하지 않은 글 입니다"));

        article.getArticleBookmarks().remove(articleBookmark);
        user.getArticleBookmarks().remove(articleBookmark);

        article.setBookmarkCount(article.getArticleBookmarks().size());
        articleBookmarkRepository.delete(articleBookmark);

        return article.getBookmarkCount();
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
    public List<ArticleResponse> getMyArticle() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        List<Article> articles = articleRepository.findByUserAndHideIsFalse(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return articles.stream().map(article -> {
            Travel travel = article.getTravel();

            Location firstLocation = travel.getLocations().get(0);
            Photo firstPhoto = firstLocation.getPhotos().get(0);

            return new ArticleResponse(
                    article.getId(),
                    article.getUser().getNickName(),
                    firstLocation.getAddress().getDegree(),
                    firstLocation.getAddress().getCity(),
                    article.getTitle(),
                    travel.getStartDate().format(formatter),
                    travel.getEndDate().format(formatter),
                    firstPhoto.getImgUrl(),
                    travel.getPhotos().size(),
                    article.getLikeCount(),
                    article.getBookmarkCount()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<ArticleResponse> getFilteredAndSortedArticles(String sort, List<Theme> themes, String degree, String city, Integer startBudget, Integer endBudget, Integer day) {
        System.out.println("get filter !! city = " + city + " degree = " + degree);
        Sort sorting = Sort.unsorted();
        if(sort != null){
            switch (sort){
                case "like":
                    sorting = Sort.by(Sort.Direction.DESC, "likeCount");
                    break;
                case "popular":
                    sorting = Sort.by(Sort.Direction.DESC, "bookmarkCount");
                    break;
                case "recent":
                    sorting = Sort.by(Sort.Direction.DESC, "id");
                    break;
                default:
                    break;
            }
        }

        Specification<Article> spec = createSpec(themes, degree, city, startBudget, endBudget, day);
        List<Article> sortedArticles = articleRepository.findAll(spec, sorting);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return sortedArticles.stream().map(article -> {
            Travel travel = article.getTravel();

            Location firstLocation = travel.getLocations().get(0);
            Photo firstPhoto = firstLocation.getPhotos().get(0);

            return new ArticleResponse(
                    article.getId(),
                    article.getUser().getNickName(),
                    firstLocation.getAddress().getDegree(),
                    firstLocation.getAddress().getCity(),
                    article.getTitle(),
                    travel.getStartDate().format(formatter),
                    travel.getEndDate().format(formatter),
                    firstPhoto.getImgUrl(),
                    travel.getPhotos().size(),
                    article.getLikeCount(),
                    article.getBookmarkCount()
            );
        }).collect(Collectors.toList());
    }

    public String autoReview(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            throw new IllegalArgumentException("Keyword list should not be empty");
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();
        ArrayNode keywordArray = mapper.createArrayNode();

        // Adding the keywords from the list (excluding the first element) to the array
        for (int i = 1; i < keywords.size(); i++) {
            keywordArray.add(keywords.get(i));
        }

        requestBody.put("location", keywords.get(0));
        requestBody.set("keywords", keywordArray);
        requestBody.put("emoticon", false);
        requestBody.put("tone", "잔잔한");
        requestBody.put("temp", 0.7);

        WebClient webClient = WebClient.create("http://210.91.210.243:7860");

        Mono<String> response = webClient.post()
                .uri("/review_generator")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody.toString())
                .retrieve()
                .bodyToMono(String.class);

        try {
            return response.block();
        } catch (Exception e) {
            throw new RuntimeException("Review generation failed", e);
        }
    }
}
