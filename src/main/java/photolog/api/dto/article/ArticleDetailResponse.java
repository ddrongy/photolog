package photolog.api.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import photolog.api.domain.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailResponse {
    private Long articleId;
    private String nickname;
    private String title;
    private String summary;
    private List<DayDTO> days;
    private Integer budget;
    private Member member;
    private List<Theme> theme;

    private Integer likes;
    private Boolean likeStatus;
    private Integer bookmarks;
    private Boolean bookmarkStatus;

    public ArticleDetailResponse(Article article, Travel travel, Boolean like, Boolean bookmark) {
        this.articleId = article.getId();
        this.nickname = article.getUser().getNickName();
        this.title = article.getTitle();
        this.summary = article.getSummary();
        this.days = travel.getDays().stream()
                .map(DayDTO::new)
                .collect(Collectors.toList());
        this.budget = article.getBudget();
        this.member =article.getMember();
        this.theme = travel.getTheme().stream().collect(Collectors.toList());

        this.likes = article.getLikeCount();
        this.likeStatus=like;
        this.bookmarks = article.getBookmarkCount();
        this.bookmarkStatus=bookmark;
    }

    @Getter
    @NoArgsConstructor
    public static class DayDTO {

        private Long dayID;
        private Integer sequence;
        private String date;
        private List<LocationDTO> locations;

        public DayDTO(Day day) {
            this.dayID = day.getId();
            this.sequence = day.getSequence();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            this.date = day.getDate().format(formatter);
            this.locations = day.getLocations().stream()
                    .map(LocationDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class LocationDTO {

        private Long locationId;
        private String name;
        private String degree;
        private String city;
        private String content;
        private List<Long> photoIds;
        private List<String> photoUrls;
        private Coordinate coordinate;

        public LocationDTO(Location location) {
            this.locationId = location.getId();
            this.name = location.getName();
            this.degree = location.getAddress().getDegree();
            this.city = location.getAddress().getCity();
            this.content = location.getContent();
            this.photoIds = location.getPhotos().stream()
                    .filter(photo -> photo.getHide() == false)
                    .map(Photo::getId)
                    .collect(Collectors.toList());
            this.photoUrls = location.getPhotos().stream()
                    .filter(photo -> photo.getHide() == false)
                    .map(Photo::getImgUrl)
                    .collect(Collectors.toList());
            this.coordinate = location.getCoordinate();
        }
    }
}
