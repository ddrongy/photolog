package photolog.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import photolog.api.domain.Photo;
import photolog.api.domain.Travel;
import photolog.api.domain.User;
import photolog.api.repository.PhotoRepository;
import photolog.api.repository.TravelRepository;
import photolog.api.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelRepository travelRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    // 게시글 작성
    @Transactional
    public Long uploadTravel(String title, List<String> imgPaths) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = (String)authentication.getPrincipal();

        User user = userRepository.findByEmail(userEmail)  // Assuming you have a findByUsername method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userEmail));


        Travel travel = new Travel(title, user);
        travelRepository.save(travel);

        List<String> imgList = new ArrayList<>();
        for (String imgUrl : imgPaths) {
            Photo img = new Photo(imgUrl, travel);
            photoRepository.save(img);
            imgList.add(img.getImgUrl());
        }
        return travel.getId();
    }
}
