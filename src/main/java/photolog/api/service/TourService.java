package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.*;
import photolog.api.repository.TourBookmarkRepository;
import photolog.api.repository.TourRepository;
import photolog.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourBookmarkRepository tourBookmarkRepository;

    public List<Tour> findByTagContaining(String keyword) {
        if(keyword == null || keyword.isEmpty()){
            return tourRepository.findAll();
        }
        return tourRepository.findByTagsContaining(keyword);
    }

    public Tour searchByContentId(Long contentId) {
        return tourRepository.findByContentId(contentId);
    }

    @Transactional
    public Integer addBookmark(Long tourId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("tour does not exist."));

        if (tourBookmarkRepository.findByTourAndUser(tour, user).isPresent()) {
            throw new IllegalArgumentException("이미 북마크 한 글 입니다");
        }

        TourBookmark tourBookmark = new TourBookmark(tour, user, LocalDateTime.now());

        tour.getTourBookmarks().add(tourBookmark);
        user.getTourBookmarks().add(tourBookmark);

        tour.setBookmarkCount(tour.getTourBookmarks().size());
        tourBookmarkRepository.save(tourBookmark);

        return tour.getTourBookmarks().size();
    }


    @Transactional
    public Integer cancelBookmark(Long tourId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("tour does not exist."));

        TourBookmark tourBookmark = tourBookmarkRepository.findByTourAndUser(tour, user)
                .orElseThrow(() -> new IllegalArgumentException("북마크 하지 않은 글 입니다"));

        tour.getTourBookmarks().remove(tourBookmark);
        user.getTourBookmarks().remove(tourBookmark);

        tour.setBookmarkCount(tour.getTourBookmarks().size());
        tourRepository.save(tour);

        tourBookmarkRepository.delete(tourBookmark);

        return tour.getTourBookmarks().size();
    }
}