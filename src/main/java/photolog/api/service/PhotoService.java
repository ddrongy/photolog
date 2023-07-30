package photolog.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import photolog.api.domain.*;
import photolog.api.domain.Address;
import photolog.api.dto.photo.LocationIdRequest;
import photolog.api.dto.photo.LocationResponse;
import photolog.api.repository.LocationRepository;
import photolog.api.repository.PhotoRepository;
import photolog.api.repository.TravelRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import okhttp3.*;

@RequiredArgsConstructor
@Service
public class PhotoService {
    
    private final PhotoRepository photoRepository;
    private final LocationRepository locationRepository;
    private final TravelRepository travelRepository;

    @Transactional
    public Long photoSave(Long travelId, String imgUrl, String stringDateTime, Coordinate coordinate, Address address, MultipartFile multipartFile) throws IOException {
        // travel 조회
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(()-> new IllegalArgumentException("photo 존재하지 않음"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(stringDateTime, formatter);

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);


        multipartBodyBuilder.addFormDataPart("image", multipartFile.getOriginalFilename(),
                RequestBody.create(multipartFile.getBytes(), MediaType.parse(multipartFile.getContentType())));

        Request request = new Request.Builder()
                .url("http://210.91.210.243:7860/mplug_hashtags")
                .post(multipartBodyBuilder.build())
                .build();
        Response response = client.newCall(request).execute();

        String responseBody = response.body().string(); // 위에서 얻은 JSON 문자열

        ObjectMapper mapper = new ObjectMapper();
        List<String> hashtags = mapper.readValue(responseBody, new TypeReference<List<String>>(){});
        String joinedHashtags = String.join(", ", hashtags);
        System.out.println(joinedHashtags);
        // photo 생성
        Photo photo = Photo.createPhoto(travel, imgUrl, dateTime, coordinate, address, joinedHashtags);
        photoRepository.save(photo);

        return photo.getId();
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
