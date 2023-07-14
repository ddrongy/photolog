package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.*;
import photolog.api.repository.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;


@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRepository travelRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final DayRepository dayRepository;
    private final PhotoRepository photoRepository;

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

    @Transactional
    public void calTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        List<Photo> photos = travel.getPhotos();

        photos.sort(Comparator.comparing(p -> p.getDateTime().toLocalDate()));

        Map<LocalDate, List<Photo>> photosByDate = photos.stream()
                .collect(Collectors.groupingBy(photo -> photo.getDateTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        LocalDate startDate = photosByDate.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.MAX);
        LocalDate endDate = photosByDate.keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.MIN);

        int totalDays = (int) DAYS.between(startDate, endDate) + 1;

        for (int i = 0; i < totalDays; i++) {
            LocalDate currentDayDate = startDate.plusDays(i);
            Day day = Day.createDay(i + 1, currentDayDate, travel);
            dayRepository.save(day);

            List<Photo> photosOfTheDay = photosByDate.getOrDefault(currentDayDate, new ArrayList<>());

            Map<Coordinate, List<Photo>> photosByLocation = photosOfTheDay.stream()
                    .collect(Collectors.groupingBy(Photo::getCoordinate));

            for (Map.Entry<Coordinate, List<Photo>> locationEntry : photosByLocation.entrySet()) {
                Location location = locationRepository.findByCoordinateAndDateAndTravelId(locationEntry.getKey(), currentDayDate, travel.getId())
                        .orElseGet(() -> {
                            Address address = locationEntry.getValue().get(0).getAddress();
                            Location newLocation = Location.createLocation(travel, locationEntry.getKey(), currentDayDate, day, address);
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

        travel.updateDate(startDate, endDate, totalDays);
        travelRepository.save(travel);
    }
}
