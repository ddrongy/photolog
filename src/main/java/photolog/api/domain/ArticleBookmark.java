package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_bookmarks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArticleBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "bookmark_time")
    private LocalDateTime bookmarkTime;

    public ArticleBookmark(Article article, User user, LocalDateTime bookmarkTime) {
        this.article = article;
        this.user = user;
        this.bookmarkTime = bookmarkTime;
    }
}