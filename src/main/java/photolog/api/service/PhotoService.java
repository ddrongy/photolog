package photolog.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import photolog.api.domain.*;
import photolog.api.dto.Photo.LocationIdRequest;
import photolog.api.dto.Photo.LocationResponse;
import photolog.api.repository.LocationRepository;
import photolog.api.repository.PhotoRepository;
import photolog.api.repository.TravelRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PhotoService {
    
    private final PhotoRepository photoRepository;
    private final LocationRepository locationRepository;
    private final TravelRepository travelRepository;
    
    public Long photoSave(Long travelId, String imgUrl, String stringDateTime, Coordinate coordinate, Address address){
        // travel 조회
        Travel travel = travelRepository.findById(travelId).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(stringDateTime, formatter);

        // photo 생성
        Photo photo = Photo.createPhoto(travel, imgUrl, dateTime, coordinate, address);
        photoRepository.save(photo);

        return photo.getId();
    }

    public List<LocationResponse> getOtherLocations(Long photoId){
        //photo 조회
        Photo photo = photoRepository.findById(photoId).get();

        //photo 에 연결된 location 조회
        Location location = photo.getLocation();

        List<Location> locations = locationRepository.findAll();
        locations.remove(location);

        // Location 리스트를 LocationResponse 리스트로 변환
        return locations.stream()
                .map(loc -> new LocationResponse(loc.getId(), loc.getSequence(), loc.getAddress().getFullAddress(), loc.getName()))
                .collect(Collectors.toList());

    }

    public Long changeLocation(Long photoId, LocationIdRequest request){
        //photo 조회
        Photo photo = photoRepository.findById(photoId).get();

        Location newLocation = locationRepository.findById(request.getLocationId()).get();

        photo.changeLocation(newLocation);
        newLocation.addPhoto(photo);

        locationRepository.save(newLocation);


        return newLocation.getId();
    }

}
