package photolog.api.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyArticleResponse {
    private String city;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String thumbnail;
    private Integer photoCnt;
    private Integer likes;
//    private Integer Bookmarks;
}
