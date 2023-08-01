package photolog.api.dto.photo;

import photolog.api.domain.Article;
import photolog.api.domain.Photo;
import photolog.api.domain.Tour;
import photolog.api.domain.Travel;

public class TagDetailResponse {
    private Long articleId;
    private String imageUrl;
    private String title;
    private String locationContent;

    public TagDetailResponse(Photo photo) {
        Travel travel = photo.getTravel();
        Article article = travel.getArticle();
        this.articleId = article.getId();
        this.imageUrl = photo.getImgUrl();
        this.title = article.getTitle();
        this.locationContent = photo.getLocation().getContent();
    }
}
