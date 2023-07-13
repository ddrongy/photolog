package photolog.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "seq", nullable = false)
    private Integer sequence;

    @Column(name = "day_date", nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "day")
    private final List<Location> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Travel travel;

    public void setTravel (Travel travel) {
        this.travel = travel;
        travel.getDays().add(this);
    }

    //== 생성 메서드 ==//
    public static Day createDay(Integer sequence, LocalDate date, Travel travel){
        Day day = new Day();
        day.setTravel(travel);
        day.setSequence(sequence);
        day.setDate(date);

        return day;
    }

    private void setSequence (Integer sequence) {
        this.sequence = sequence;
    }
    private void setDate (LocalDate date) {
        this.date = date;
    }

}
