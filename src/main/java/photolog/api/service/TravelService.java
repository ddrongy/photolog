package photolog.api.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.*;
import photolog.api.dto.Travel.CalculateResultResponse;
import photolog.api.dto.Travel.TitleRequest;
import photolog.api.repository.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final DayRepository dayRepository;
    private final PhotoRepository photoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // 기본 travel 생성
    public Long createTravel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)  // Assuming you have a findByUsername method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));
        Travel travel = new Travel(user);

        travelRepository.save(travel);

        return travel.getId();
    }

    // travel - day - location - photo 계산
    @Transactional
    public List<Long> calTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        List<Photo> photos = travel.getPhotos();

        photos.sort(Comparator.comparing(p -> p.getDateTime().toLocalDate()));

        Map<LocalDate, List<Photo>> photosByDate = photos.stream()
                .collect(Collectors.groupingBy(photo -> photo.getDateTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        LocalDate startDate = photosByDate.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.MAX);
        LocalDate endDate = photosByDate.keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.MIN);

        AtomicInteger sequence = new AtomicInteger(1);
        for (Map.Entry<LocalDate, List<Photo>> dateEntry : photosByDate.entrySet()) {
            LocalDate currentDayDate = dateEntry.getKey();
            Day day = dayRepository.findByDateAndTravelId(currentDayDate, travel.getId())
                    .map(existingDay -> {
                        sequence.set(existingDay.getSequence());
                        return existingDay;
                    })
                    .orElseGet(() -> {
                        Day newDay = Day.createDay(sequence.getAndIncrement(), currentDayDate, travel);
                        dayRepository.save(newDay);
                        return newDay;
                    });

            Map<Coordinate, List<Photo>> photosByLocation = dateEntry.getValue().stream()
                    .collect(Collectors.groupingBy(Photo::getCoordinate));

            for (Map.Entry<Coordinate, List<Photo>> locationEntry : photosByLocation.entrySet()) {
                Location location = locationRepository.findByCoordinateAndDateAndTravelId(locationEntry.getKey(), currentDayDate, travel.getId())
                        .orElseGet(() -> {
                            Address address = locationEntry.getValue().get(0).getAddress();
                            Location newLocation = Location.createLocation(travel, locationEntry.getKey(), currentDayDate, day, address, sequence.get());
                            locationRepository.save(newLocation);
                            return newLocation;
                        });

                List<Photo> photosInSameLocation = locationEntry.getValue();
                for (Photo photo : photosInSameLocation) {
                    photo.setLocation(location);
                    photoRepository.save(photo);
                }
            }
        }

        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        travel.updateDate(startDate, endDate, totalDays);
        travelRepository.save(travel);


        return travel.getLocations().stream()
                .map(Location::getId)
                .collect(Collectors.toList());
    }

    public CalculateResultResponse calculateResult(Long travelId){
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        return new CalculateResultResponse(travel.getTotalDate()-1, travel.getTotalDate(),
                travel.getStartDate(), travel.getEndDate(), travel.getLocations().size());
    }

    public String updateTitle(Long travelId, TitleRequest request) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        travel.updateTitle(request.getTitle());
        travelRepository.save(travel);

        return travel.getTitle();
    }
}
