package photolog.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.domain.Article;
import photolog.api.dto.Article.AddArticleRequest;
import photolog.api.dto.Location.LocationResponse;
import photolog.api.dto.ResponseDto;
import photolog.api.service.ArticleService;

@Tag(name = "article", description = "게시 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/{travelId}")
    public ResponseEntity<ResponseDto<Long>> addArticle(@PathVariable Long travelId, @RequestBody AddArticleRequest request) {
        Long savedId = articleService.save(travelId, request);

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("save article successful.");
        response.setData(savedId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
