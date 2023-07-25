package photolog.api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "travel_id", nullable = true)
    private Travel travel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private Integer likeCount;
    private Integer reportCount;
    private Boolean hide;
    private Integer bookmarks;
    private Integer budget;  //20, 40, 60, 80, 100

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<ArticleReport> reports = new ArrayList<>();

    @Builder
    public Article(Travel travel, User user) {
        this.travel = travel;
        travel.setArticle(this);
        this.user = user;
        user.getArticles().add(this);
        this.likeCount = 0;
        this.reportCount = 0;
        this.bookmarks = 0;
        this.hide = false;
    }

    public void addBookmark(){
        this.bookmarks++;
    }

    public void setLikeCount(Integer count) {
        this.likeCount = count;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
        if (reportCount>=5) this.hide = true;
    }
    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setBudget(Integer budget) {this.budget = budget;}

    public void setHide(boolean hide) {
        this.hide = hide;
    }
}
