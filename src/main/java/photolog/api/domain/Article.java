package photolog.api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;

@Getter
@Entity
@Table(name = "article")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "travel_id")
    private Travel travel;

    private void setTravel(@NotNull Travel travel) {
        travel.setArticle(this);
    }

    @Builder
    public Article(String title, String content, Travel travel) {
        this.title = title;
        this.content = content;
        this.travel = travel;
        setTravel(travel);
    }
}
