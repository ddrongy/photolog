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
import photolog.api.dto.travel.*;
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


    public Travel getTravelById(Long travelId) {
        return travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));
    }

    // 기본 travel 생성
    @Transactional
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
    public CalculateResponse calTravel(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        List<Photo> photos = travel.getPhotos();

        photos.sort(Comparator.comparing(p -> p.getDateTime().toLocalDate()));

        Map<LocalDate, List<Photo>> photosByDate = photos.stream()
                .collect(Collectors.groupingBy(photo -> photo.getDateTime().toLocalDate(), TreeMap::new, Collectors.toList()));

        LocalDate startDate = photosByDate.keySet().stream().min(LocalDate::compareTo).orElse(LocalDate.MAX);
        LocalDate endDate = photosByDate.keySet().stream().max(LocalDate::compareTo).orElse(LocalDate.MIN);

        LocalDate prevDayDate = null;  // 이전 날짜를 저장하기 위한 변수를 추가
        AtomicInteger sequence = new AtomicInteger(1);
        for (Map.Entry<LocalDate, List<Photo>> dateEntry : photosByDate.entrySet()) {
            LocalDate currentDayDate = dateEntry.getKey();

            // 이전 날짜가 null이 아니면, 즉 두 번째 날짜부터는 이전 날짜와의 차이를 확인
            if (prevDayDate != null) {
                long daysBetween = ChronoUnit.DAYS.between(prevDayDate, currentDayDate);
                // 차이가 1일보다 크면 에러를 발생시킴
                if (daysBetween > 1) {
                    throw new IllegalArgumentException("Days are not consecutive. Found a gap at date: " + currentDayDate);
                }
            }
            Day day = dayRepository.findByDateAndTravelId(currentDayDate, travel.getId())
                    .map(existingDay -> {
                        sequence.set(existingDay.getSequence());
                        return existingDay;
                    })
                    .orElseGet(() -> {
                        Day newDay = Day.createDay(sequence.get(), currentDayDate, travel);
                        dayRepository.save(newDay);
                        sequence.getAndIncrement();
                        return newDay;
                    });

            Map<Coordinate, List<Photo>> photosByLocation = dateEntry.getValue().stream()
                    .collect(Collectors.groupingBy(Photo::getCoordinate));

            photosByLocation.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparing(photosList -> photosList.get(0).getDateTime())))
                    .forEach(locationEntry -> {
                        Location location = locationRepository.findByCoordinateAndDateAndTravelId(locationEntry.getKey(), currentDayDate, travel.getId())
                                .orElseGet(() -> {
                                    Address address = locationEntry.getValue().get(0).getAddress();
                                    Location newLocation = Location.createLocation(travel, locationEntry.getKey(), currentDayDate, day, address, day.getSequence());
                                    locationRepository.save(newLocation);
                                    return newLocation;
                                });

                        List<Photo> photosInSameLocation = locationEntry.getValue();
                        for (Photo photo : photosInSameLocation) {
                            photo.setLocation(location);
                            photoRepository.save(photo);
                        }
                    });
            prevDayDate = currentDayDate;  // 이전 날짜를 현재 날짜로 업데이트

        }

        int totalDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        travel.updateDate(startDate, endDate, totalDays);
        travelRepository.save(travel);

        entityManager.flush();
        entityManager.refresh(travel);

        int night = travel.getTotalDate();
        if (travel.getTotalDate() > 0) {
            night--;
        }

        List<Long> locationIdList = travel.getLocations().stream()
                .map(Location::getId)
                .collect(Collectors.toList());

        List<String> locationAddressList = travel.getLocations().stream()
                .map(location -> location.getAddress().getFullAddress())
                .collect(Collectors.toList());

        List<String> locationImgList = travel.getLocations().stream()
                .filter(location -> location.getPhotos() != null && !location.getPhotos().isEmpty())
                .map(location -> location.getPhotos().get(0).getImgUrl())
                .collect(Collectors.toList());

        return new CalculateResponse(night, travel.getTotalDate(),
                travel.getStartDate(), travel.getEndDate(),
                travel.getLocations().size(), travel.getPhotos().size(), locationIdList,locationAddressList, locationImgList);
    }


    @Transactional
    public String updateTitle(Long travelId, TitleRequest request) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        travel.updateTitle(request.getTitle());
        travelRepository.save(travel);

        return travel.getTitle();
    }

    @Transactional
    public Theme updateTheme(Long travelId, ThemeRequest request) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));

        travel.updateTheme(request.getTheme());
        travelRepository.save(travel);

        return travel.getTheme();
    }

    @Transactional
    public SummaryTextResponse textSummary(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));
        return new SummaryTextResponse(travel);
    }

    @Transactional
    public SummaryMapResponse mapSummary(Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("Travel not found with id: " + travelId));
        return new SummaryMapResponse(travel);
    }

    @Transactional
    public List<MyLogResponse> getMyLog() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String) authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));

        List<Travel> travels = travelRepository.findByUser(user);

        return travels.stream().map(travel -> {
            Location firstLocation = travel.getLocations().get(0);
            Photo firstPhoto = firstLocation.getPhotos().get(0);

            return new MyLogResponse(
                    travel.getId(),
                    firstLocation.getAddress().getCity(),
                    travel.getTitle(),
                    travel.getStartDate(),
                    travel.getEndDate(),
                    firstPhoto.getImgUrl(),
                    travel.getPhotos().size()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long travelId) {
        travelRepository.deleteById(travelId);
    }
}
