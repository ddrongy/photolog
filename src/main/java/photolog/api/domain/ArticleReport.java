package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "article_reports")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "report_time")
    private LocalDateTime reportTime;

    public ArticleReport(Article article, User user, LocalDateTime reportTime) {
        this.article = article;
        this.user = user;
        this.reportTime = reportTime;
    }
}