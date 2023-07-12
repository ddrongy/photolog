package photolog.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import photolog.api.dto.ResponseDto;
import photolog.api.service.S3Service;
import photolog.api.service.TravelService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;
    private final S3Service s3Service;

    @PostMapping("/post")
    public ResponseEntity<ResponseDto<Long>> uploadPost(@RequestPart("content") String title,
                                                        @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new IllegalArgumentException("이미지 입력 없음");
        }
        List<String> imgPaths = s3Service.upload(multipartFiles);
        System.out.println("IMG 경로들 : " + imgPaths);
        Long savedId = travelService.uploadTravel(title, imgPaths);

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Travel registration successful.");
        response.setData(savedId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
