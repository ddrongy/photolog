package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "article_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArticleLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "like_time")
    private LocalDateTime likeTime;

    public ArticleLike(Article article, User user, LocalDateTime likeTime) {
        this.article = article;
        this.user = user;
        this.likeTime = likeTime;
    }
}