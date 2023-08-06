package photolog.api.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ArticleResponse {
    private Long id;
    private String nickname;
    private String degree;
    private String city;
    private String title;
    private String startDate;
    private String endDate;
    private String thumbnail;
    private Integer photoCnt;
    private Integer likes;
    private Integer Bookmarks;
}
