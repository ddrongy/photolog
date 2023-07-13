package photolog.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import photolog.api.domain.Coordinate;
import photolog.api.dto.ResponseDto;
import photolog.api.service.PhotoService;
import photolog.api.service.S3Service;

@Tag(name = "photo", description = "사진 API")
@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final S3Service s3Service;

    @PostMapping("/save/{travelId}")
    public ResponseEntity<ResponseDto<Long>> savePhoto(@PathVariable Long travelId,
                                                       @RequestPart("img") MultipartFile multipartFile,
                                                       @RequestPart("dateTime") String dateTime,
                                                       @RequestPart("log") Double log,
                                                       @RequestPart("lat") Double lat){

        String imgPath = s3Service.uploadOne(multipartFile);
        photoService.photoSave(travelId, imgPath, dateTime, new Coordinate(log, lat));

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Travel create successful.");
        response.setData(travelId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
