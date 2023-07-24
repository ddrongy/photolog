package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import photolog.api.domain.Location;
import photolog.api.domain.Photo;
import photolog.api.dto.Location.DescriptionRequest;
import photolog.api.dto.Location.LocationResponse;
import photolog.api.dto.Location.NameAndDescriptionRequest;
import photolog.api.dto.Location.NameRequest;
import photolog.api.repository.LocationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;
    @Transactional
    public LocationResponse getLocation(Long id) {
        Location location = locationRepository.findById(id).get();

        List<Photo> photos = location.getPhotos();

        List<String> imgUrls = photos.stream()
                .map(Photo::getImgUrl)
                .collect(Collectors.toList());

        List<Long> photoIds = photos.stream()
                .map(Photo::getId)
                .collect(Collectors.toList());

        return new LocationResponse(location.getId(), location.getSequence(), location.getDate(),
                photoIds, imgUrls, location.getAddress().getFullAddress(), location.getName(), location.getDescription());
    }
    @Transactional
    public String updateDescription(Long locationId, @NotNull DescriptionRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        location.updateDescription(request.getDescription());
        locationRepository.save(location);

        return location.getDescription();
    }
    @Transactional
    public String updateName(Long locationId, @NotNull NameRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        location.updateName(request.getName());
        locationRepository.save(location);

        return location.getName();
    }

    @Transactional
    public void updateNameAndDescription(Long locationId, @NotNull NameAndDescriptionRequest request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        location.updateName(request.getName());
        location.updateDescription(request.getDescription());
        locationRepository.save(location);

    }
}
