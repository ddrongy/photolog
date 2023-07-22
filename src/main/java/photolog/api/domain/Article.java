package photolog.api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

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

    private Integer likes;
    private Integer reports;
    private Boolean hide;
    private Integer bookmarks;


    @Builder
    public Article(Travel travel, User user) {
        this.travel = travel;
        travel.setArticle(this);
        this.user = user;
        user.getArticles().add(this);
        this.likes = 0;
        this.reports = 0;
        this.bookmarks = 0;
        this.hide = false;
    }

    public void addBookmark(){
        this.bookmarks++;
    }

    public void addLike(){
        this.likes++;
    }
    public void cancelLike(){
        this.likes--;
    }

    public void addReport(){
        this.reports++;
        if(this.reports >=5)  this.hide=true;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

}
