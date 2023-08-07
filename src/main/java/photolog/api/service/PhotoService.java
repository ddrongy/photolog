package photolog.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import photolog.api.domain.*;
import photolog.api.domain.Address;
import photolog.api.dto.photo.*;
import photolog.api.repository.LocationRepository;
import photolog.api.repository.PhotoRepository;
import photolog.api.repository.TravelRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PhotoService {
    
    private final PhotoRepository photoRepository;
    private final LocationRepository locationRepository;
    private final TravelRepository travelRepository;

    private WebClient translateWebClient =
            WebClient
                    .builder()
                    .baseUrl("http://210.91.210.243:7860")
                    .build();

    @Transactional
    public Long photoSave(Long travelId, String imgUrl, String stringDateTime, Coordinate coordinate, Address address) throws IOException, JSONException {
        // travel 조회
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(stringDateTime, formatter);

        Photo photo = Photo.createPhoto(travel, imgUrl, dateTime, coordinate, address);
        photoRepository.save(photo);


        return photo.getId();
    }

    public Page<PhotoTagResponse> findByTagContaining(List<String> keywords, Pageable pageable) {
        Page<Photo> photos;
        if (keywords == null || keywords.isEmpty()) {
            photos = photoRepository.findAll(pageable);
        }
        else {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode input = objectMapper.createObjectNode().putPOJO("tags", keywords);

            try {
                String response = translateWebClient
                        .post()
                        .uri("/translator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(input), JsonNode.class)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JsonNode translationRoot = objectMapper.readTree(response);
                List<String> translatedKeywords = Arrays.asList(objectMapper.convertValue(translationRoot, String[].class));
                System.out.println(translatedKeywords);
                if (translatedKeywords == null || translatedKeywords.isEmpty()) {
                    photos = photoRepository.findAll(pageable);
                } else {
                    Specification<Photo> spec = Specification.where(null);
                    for (String keyword : translatedKeywords) {
                        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("tags"), "%" + keyword + "%"));
                    }
                    photos = photoRepository.findAll(spec, pageable);
                }

                return photos.map(photo -> new PhotoTagResponse(photo.getId(), photo.getImgUrl()));
            } catch (Exception e) {
                // 예외 처리 코드
                throw new RuntimeException("Translation failed", e);
            }
        }
        return photos.map(photo -> new PhotoTagResponse(photo.getId(), photo.getImgUrl()));
    }



    @Transactional
    public PhotoDetailResponse getDetailInformation(Long photoId){
        //photo 조회
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        Article article = photo.getTravel().getArticle();
        Location location = photo.getLocation();

        return new PhotoDetailResponse(
                article.getId(),
                photo.getImgUrl(),
                article.getTitle(),
                location.getName(),
                location.getContent()
                );
    }

    @Transactional
    public TagDetailResponse tagDetailResponse (Long photoId){
        //photo 조회
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        Article article = photo.getTravel().getArticle();
        Location location = photo.getLocation();

        return new TagDetailResponse(photo);
    }



    @Transactional
    public List<LocationResponse> getOtherLocations(Long photoId){
        //photo 조회
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        //photo 에 연결된 location 조회
        Location location = photo.getLocation();

        // 해당 photo와 같은 travelId를 가지는 location 조회
        List<Location> locations = locationRepository.findAllByTravelId(location.getTravel().getId());
        locations.remove(location);

        // Location 리스트를 LocationResponse 리스트로 변환
        return locations.stream()
                .map(loc -> new LocationResponse(loc.getId(), loc.getSequence(), loc.getAddress().getFullAddress(), loc.getName()))
                .collect(Collectors.toList());
    }
    @Transactional
    public Long changeLocation(Long photoId, LocationIdRequest request){
        //photo 조회
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        Location newLocation = locationRepository.findById(request.getLocationId())
                .orElseThrow(()-> new IllegalArgumentException("location 존재하지 않음"));

        photo.changeLocation(newLocation);
        newLocation.addPhoto(photo);

        locationRepository.save(newLocation);

        return newLocation.getId();
    }

    @Transactional
    public void setHide(Long photoId){
        //photo 조회
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        photo.setHide(true);
        photoRepository.save(photo);
    }
    @Transactional
    public void delete(Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("photo 존재하지 않음"));
        photo.delTravel();
        photo.delLocation();

        photoRepository.deleteById(photoId);
    }
}
