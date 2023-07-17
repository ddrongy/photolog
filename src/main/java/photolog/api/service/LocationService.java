package photolog.api.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import photolog.api.domain.Location;
import photolog.api.domain.Photo;
import photolog.api.domain.Travel;
import photolog.api.dto.Location.DescriptionRequest;
import photolog.api.dto.Location.LocationResponse;
import photolog.api.dto.Location.NameRequest;
import photolog.api.dto.Travel.TitleRequest;
import photolog.api.repository.LocationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationResponse getLocation(Long id) {
        Location location = locationRepository.findById(id).get();

        List<Photo> photos = location.getPhotos();
        List<String> imgUrls = photos.stream()
                .map(Photo::getImgUrl)
                .collect(Collectors.toList());

        List<Long> photoIds = photos.stream()
                .map(Photo::getId)
                .collect(Collectors.toList());

        return new LocationResponse(location.getId(), location.getSequence(), location.getDate(), photoIds, imgUrls, location.getAddress().getFullAddress());
    }

    public String updateDescription(Long locationId, @NotNull DescriptionRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        location.updateDescription(request.getDescription());
        locationRepository.save(location);

        return location.getDescription();
    }

    public String updateName(Long locationId, @NotNull NameRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        location.updateName(request.getName());
        locationRepository.save(location);

        return location.getName();
    }
}
