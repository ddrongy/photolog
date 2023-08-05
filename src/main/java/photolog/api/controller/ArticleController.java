package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.domain.Theme;
import photolog.api.dto.article.*;
import photolog.api.dto.ResponseDto;
import photolog.api.service.ArticleService;

import java.util.List;

@Tag(name = "articles", description = "게시글 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{travelId}")
    @Operation(summary = "게시글 작성")
    public ResponseEntity<ResponseDto<Long>> addArticle(@PathVariable Long travelId,
                                                        @RequestBody ArticleCreateRequest request) {
        Long savedId = articleService.save(travelId, request);

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("save article successful.");
        response.setData(savedId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{ArticleId}")
    @Operation(summary = "게시글 수정")
    public ResponseEntity<ResponseDto<ArticleDetailResponse>> changeTitle(@PathVariable Long ArticleId,
                                                                          @RequestBody ArticleUpdateRequest request){
        ArticleDetailResponse articleDetailResponse = articleService.updateArticle(ArticleId, request);

        ResponseDto<ArticleDetailResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("update article title successful.");
        response.setData(articleDetailResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/report/{articleId}")
    @Operation(summary = "게시글 신고")
    public ResponseEntity<ResponseDto<Void>> report(@PathVariable Long articleId) {
        articleService.addReport(articleId);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("add report successful.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/like/{articleId}")
    @Operation(summary = "게시글 like")
    public ResponseEntity<ResponseDto<Integer>> addLike(@PathVariable Long articleId) {
        Integer likes = articleService.addLike(articleId);

        ResponseDto<Integer> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("add Like successful.");
        response.setData(likes);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/like/{articleId}")
    @Operation(summary = "게시글 like 취소")
    public ResponseEntity<ResponseDto<Integer>> cancelLike(@PathVariable Long articleId) {
        Integer likes = articleService.cancelLike(articleId);

        ResponseDto<Integer> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("cancel Like successful.");
        response.setData(likes);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/bookmark/{articleId}")
    @Operation(summary = "게시글 bookmark")
    public ResponseEntity<ResponseDto<Integer>> addBookmark(@PathVariable Long articleId) {
        Integer bookmarkCount = articleService.addBookmark(articleId);

        ResponseDto<Integer> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("add bookmark successful.");
        response.setData(bookmarkCount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/bookmark/{articleId}")
    @Operation(summary = "게시글 bookmark 취소")
    public ResponseEntity<ResponseDto<Integer>> cancelBookmark(@PathVariable Long articleId) {
        Integer bookmarkCount = articleService.cancelBookmark(articleId);

        ResponseDto<Integer> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("cancel bookmark successful.");
        response.setData(bookmarkCount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "게시글 상세 조회")
    public ResponseEntity<ResponseDto<ArticleDetailResponse>> getArticle(@PathVariable Long articleId){
        ArticleDetailResponse articleDetailResponse = articleService.getArticleById(articleId);

        ResponseDto<ArticleDetailResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get article successful.");
        response.setData(articleDetailResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/travelInfo/{articleId}")
    @Operation(summary = "여행 정보 끌어오기")
    public ResponseEntity<ResponseDto<TravelInfoResponse>> getTravelInfo(@PathVariable Long articleId){
        TravelInfoResponse travelInfo = articleService.getTravelInfo(articleId);

        ResponseDto<TravelInfoResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get article successful.");
        response.setData(travelInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("")
    @Operation(summary = "내 article log 조회")
    public ResponseEntity<ResponseDto<List<ArticleResponse>>> getMyArticle() {
        List<ArticleResponse> myLog = articleService.getMyArticle();

        ResponseDto<List<ArticleResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get my article list successful.");
        response.setData(myLog);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/filtering")
    @Operation(summary = "article 정렬 및 필터링")
    public ResponseEntity<ResponseDto<List<ArticleResponse>>> getSortedArticles(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) List<Theme> theme,
            @RequestParam(required = false) Integer startBudget,
            @RequestParam(required = false) Integer endBudget,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) String degree,
            @RequestParam(required = false) String city) {
        System.out.println("get controller !! city = " + city + " degree = " + degree);
        List<ArticleResponse> sortedArticle = articleService.getFilteredAndSortedArticles(sort, theme, degree, city,  startBudget, endBudget, day);

        ResponseDto<List<ArticleResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get my article list successful.");
        response.setData(sortedArticle);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/review/{locationID}")
    @Operation(summary = "자동 리뷰 생성")
    public ResponseEntity<ResponseDto<String>> searchPhotoByTagsAndLocation(
            @PathVariable Long locationID,
            @RequestParam(required = false) List<String> keyword) {

        String review = articleService.autoReview(locationID, keyword);

        ResponseDto<String> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get Photo informations by keyword and locationID successful.");
        response.setData(review);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping ("/{articleId}")
    @Operation(summary = "게시글 삭제")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long articleId) {
        articleService.delete(articleId);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("article deletion successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
