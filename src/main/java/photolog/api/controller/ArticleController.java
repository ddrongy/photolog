package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.Article.ArticleResponse;
import photolog.api.dto.Article.LocationContentRequest;
import photolog.api.dto.Article.TitleRequest;
import photolog.api.dto.ResponseDto;
import photolog.api.service.ArticleService;

@Tag(name = "articles", description = "게시글 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{travelId}")
    @Operation(summary = "게시글 작성")
    public ResponseEntity<ResponseDto<ArticleResponse>> addArticle(@PathVariable Long travelId) {
        ArticleResponse addArticleResponse = articleService.save(travelId);

        ResponseDto<ArticleResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("save article successful.");
        response.setData(addArticleResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/title/{ArticleId}")
    @Operation(summary = "게시글 title 변경")
    public ResponseEntity<ResponseDto<Void>> changeTitle(@PathVariable Long ArticleId,
                                                           @RequestBody TitleRequest request){
        articleService.updateTitle(ArticleId, request);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("update article title successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/report/{articleId}")
    @Operation(summary = "게시글 신고")
    public ResponseEntity<ResponseDto<Void>> report(@PathVariable Long articleId) {
        articleService.report(articleId);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("add report successful.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/addLike/{articleId}")
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

    @PostMapping("/cancelLike/{articleId}")
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

    @PatchMapping("/location/{locationId}")
    @Operation(summary = "게시글 내 Location content 변경")
    public ResponseEntity<ResponseDto<Void>> changeTitle(@PathVariable Long locationId,
                                                         @RequestBody LocationContentRequest request){
        articleService.updateContent(locationId, request);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("update article content successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "게시글 상세 조회")
    public ResponseEntity<ResponseDto<ArticleResponse>> getArticle(@PathVariable Long articleId){
        ArticleResponse articleResponse = articleService.getArticleById(articleId);

        ResponseDto<ArticleResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get article successful.");
        response.setData(articleResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
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
