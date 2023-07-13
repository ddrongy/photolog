package photolog.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import photolog.api.dto.ResponseDto;
import photolog.api.service.S3Service;
import photolog.api.service.TravelService;

@Tag(name = "travel", description = "여행기록 API")
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;
    private final S3Service s3Service;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto<Long>> createTravel(){
        Long travelId = travelService.createTravel();

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Travel create successful.");
        response.setData(travelId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

//    @PostMapping("/post")
//    public ResponseEntity<ResponseDto<Long>> uploadPost(@RequestPart("title") String title,
//                                                        @RequestPart("img") List<MultipartFile> multipartFiles,
//                                                        @RequestPart("dateTime") List<String> dateTimes) {
//        if (multipartFiles == null) {
//            throw new IllegalArgumentException("이미지 입력 없음");
//        }
//
//        List<LocalDateTime> convertedDateTimes = dateTimes.stream()
//                .map(LocalDateTime::parse)
//                .collect(Collectors.toList());
//
//        List<String> imgPaths = s3Service.upload(multipartFiles);
//        System.out.println("IMG 경로들 : " + imgPaths);
//        Long savedId = travelService.uploadTravel(title, imgPaths, convertedDateTimes);
//
//        ResponseDto<Long> response = new ResponseDto<>();
//        response.setStatus(true);
//        response.setMessage("Travel registration successful.");
//        response.setData(savedId);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(response);
//    }
}
