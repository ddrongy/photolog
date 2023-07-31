package photolog.api.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDetailResponse {
    private Long articleId;
    private String photoUrl;
    private String articleTitle;
    private String locationName;
    private String locationContent;
}
