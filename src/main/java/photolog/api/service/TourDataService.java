package photolog.api.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import photolog.api.domain.TourData;
import photolog.api.repository.TourDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourDataService {

    private final TourDataRepository tourDataRepository;

    public List<TourData> findByTagContaining(String keyword) {
        if(keyword == null || keyword.isEmpty()){
            return tourDataRepository.findAll();
        }
        return tourDataRepository.findByTagsContaining(keyword);
    }

    public TourData searchByContentId(Long contentId) {
        return tourDataRepository.findByContentId(contentId);
    }
}