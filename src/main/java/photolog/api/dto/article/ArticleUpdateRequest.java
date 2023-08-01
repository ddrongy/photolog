package photolog.api.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.Member;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUpdateRequest {
    private String title;
    private String summary;
    private List<String> locationContent;
    private Integer budget;
    private Member member;
}
