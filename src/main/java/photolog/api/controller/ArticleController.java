package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ResponseDto<ArticleCreateResponse>> addArticle(@PathVariable Long travelId) {
        ArticleCreateResponse addArticleCreateResponse = articleService.save(travelId);

        ResponseDto<ArticleCreateResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("save article successful.");
        response.setData(addArticleCreateResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{ArticleId}")
    @Operation(summary = "게시글 수정")
    public ResponseEntity<ResponseDto<ArticleResponse>> changeTitle(@PathVariable Long ArticleId,
                                                                          @RequestBody ArticleUpdateRequest request){
        ArticleResponse articleResponse = articleService.updateArticle(ArticleId, request);

        ResponseDto<ArticleResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("update article title successful.");
        response.setData(articleResponse);

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
    @GetMapping("")
    @Operation(summary = "내 article log 조회")
    public ResponseEntity<ResponseDto<List<MyArticleResponse>>> getMyArticle() {
        List<MyArticleResponse> myLog = articleService.getMyArticle();

        ResponseDto<List<MyArticleResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get my article list successful.");
        response.setData(myLog);

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
