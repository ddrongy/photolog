package photolog.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.Article.AddArticleResponse;
import photolog.api.dto.ResponseDto;
import photolog.api.dto.User.ArticleResponse;
import photolog.api.service.ArticleService;

import java.util.List;

@Tag(name = "articles", description = "게시 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{travelId}")
    public ResponseEntity<ResponseDto<AddArticleResponse>> addArticle(@PathVariable Long travelId) {
        AddArticleResponse addArticleResponse = articleService.save(travelId);

        ResponseDto<AddArticleResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("save article successful.");
        response.setData(addArticleResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseDto<List<ArticleResponse>>> getArticle(@PathVariable Long userId){
        List<ArticleResponse> userArticle = articleService.getUserArticle(userId);

        ResponseDto<List<ArticleResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get article successful.");
        response.setData(userArticle);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
