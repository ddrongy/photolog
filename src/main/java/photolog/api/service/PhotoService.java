package photolog.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import photolog.api.domain.Location;
import photolog.api.domain.Photo;
import photolog.api.domain.Travel;
import photolog.api.repository.PhotoRepository;
import photolog.api.repository.TravelRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PhotoService {
    
    private final PhotoRepository photoRepository;
    private final TravelRepository travelRepository;
    
    public Long photoSave(Long travelId, String imgUrl, String stringDateTime, Location location){
        // travel 조회
        Travel travel = travelRepository.findById(travelId).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(stringDateTime, formatter);

        // photo 생성
        Photo photo = Photo.createPhoto(travel, imgUrl, dateTime, location);
        photoRepository.save(photo);

        return photo.getId();
    }
}
