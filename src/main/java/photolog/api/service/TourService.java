package photolog.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import photolog.api.domain.*;
import photolog.api.dto.tour.TagDetailResponse;
import photolog.api.dto.tour.TourBookmarkResponse;
import photolog.api.dto.tour.TourResponse;
import photolog.api.repository.TourBookmarkRepository;
import photolog.api.repository.TourRepository;
import photolog.api.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourService {

    @Value("${tourServiceKey}")
    private String serviceKey;

    private WebClient webClient =
            WebClient
                    .builder()
                    .baseUrl("https://apis.data.go.kr/B551011/KorService1")
                    .build();

    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final TourBookmarkRepository tourBookmarkRepository;

    public Page<TourResponse> findByTagContaining(String keyword, Pageable pageable) {
        Page<Tour> tours;
        if(keyword == null || keyword.isEmpty()){
            tours = tourRepository.findAll(pageable);
        } else {
            tours = tourRepository.findByTagsContaining(keyword, pageable);
        }
        return tours.map(TourResponse::new);
    }

    public TagDetailResponse searchByContentId(Long contentId) throws JsonProcessingException {
        Tour tour = tourRepository.findByContentId(contentId);

        // api 요청
        String response =
                webClient
                        .get()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/detailCommon1")
                                        .queryParam("MobileOS", "IOS")
                                        .queryParam("MobileApp", "photolog")
                                        .queryParam("_type", "json")
                                        .queryParam("contentId", contentId)
                                        .queryParam("contentTypeId", 12)
                                        .queryParam("areacodeYN", "N")
                                        .queryParam("catcodeYN", "N")
                                        .queryParam("addrinfoYN", "N")
                                        .queryParam("mapinfoYN", "N")
                                        .queryParam("overviewYN", "Y")
                                        .queryParam("serviceKey", serviceKey)
                                        .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        // 결과 확인
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response);
        String overview = root.path("response").path("body").path("items").path("item").get(0).path("overview").asText();

        // api 요청
        String response2 =
                webClient
                        .get()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/detailIntro1")
                                        .queryParam("MobileOS", "IOS")
                                        .queryParam("MobileApp", "photolog")
                                        .queryParam("_type", "json")
                                        .queryParam("contentId", contentId)
                                        .queryParam("contentTypeId", 12)
                                        .queryParam("serviceKey", serviceKey)
                                        .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        // 결과 확인
        ObjectMapper mapper2 = new ObjectMapper();
        JsonNode root2 = mapper2.readTree(response2);
        String infocenter = root2.path("response").path("body").path("items").path("item").get(0).path("infocenter").asText();
        String restdate = root2.path("response").path("body").path("items").path("item").get(0).path("restdate").asText();
        String usetime = root2.path("response").path("body").path("items").path("item").get(0).path("usetime").asText();


        return new TagDetailResponse(tour, overview, infocenter, restdate, usetime);
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