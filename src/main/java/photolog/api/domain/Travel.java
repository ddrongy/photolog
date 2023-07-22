package photolog.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "travels")
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String title;

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.REMOVE)
    private final List<Day> days = new ArrayList<>();

    @OneToMany(mappedBy = "travel")
    private final List<Location> locations = new ArrayList<>();

    @OneToMany(mappedBy = "travel")
    private final List<Photo> photos = new ArrayList<>();

    @OneToOne(mappedBy = "travel", cascade = CascadeType.REMOVE)
    private Article article;

    public Travel(User user) {
        this.user = user;
        user.getTravels().add(this);
    }

    public void updateDate(LocalDate startDate, LocalDate endDate, Integer totalDate){
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDate = totalDate;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
