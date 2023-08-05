package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import photolog.api.domain.Address;
import photolog.api.domain.Coordinate;

import photolog.api.dto.photo.*;
import photolog.api.dto.ResponseDto;
import photolog.api.service.PhotoService;
import photolog.api.service.S3Service;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;

@Tag(name = "photo", description = "사진 API")
@RestController
@RequestMapping("/api/photo")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final S3Service s3Service;

    @PostMapping("/save/{travelId}")
    @Operation(summary = "입력받은 travelId에 photo 추가")
    public ResponseEntity<ResponseDto<Long>> savePhoto(@PathVariable Long travelId,
                                                       @RequestPart("img") MultipartFile multipartFile,
                                                       @RequestPart("dateTime") String dateTime,
                                                       @RequestPart("log") Double log,
                                                       @RequestPart("lat") Double lat,
                                                       @RequestPart("fullAddress") String fullAddress
                                                       ) throws IOException, JSONException {

        String imgPath = s3Service.uploadOne(multipartFile);

        photoService.photoSave(travelId, imgPath, dateTime, new Coordinate(log, lat), new Address(fullAddress), multipartFile);

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage(travelId + "에 Photo 추가 성공");
        response.setData(travelId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/otherLocation/{photoId}")
    @Operation(summary = "photo에 연결되지 않은 location 정보 불러오기")
    public ResponseEntity<ResponseDto<List<LocationResponse>>> getOtherLocations(@PathVariable Long photoId){

        List<LocationResponse> otherLocations = photoService.getOtherLocations(photoId);

        ResponseDto<List<LocationResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get other location information successful.");
        response.setData(otherLocations);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping ("/changeLocation/{photoId}")
    @Operation(summary = "photo에 연결된 location 정보 변경하기")
    public ResponseEntity<ResponseDto<Long>> changeLocation(@PathVariable Long photoId,
                                                            @RequestBody LocationIdRequest request){
        Long changedLocation = photoService.changeLocation(photoId, request);

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("photo change location successful.");
        response.setData(changedLocation);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping ("/hide/{photoId}")
    @Operation(summary = "photo 숨김 처리")
    public ResponseEntity<ResponseDto<Void>> changeLocation(@PathVariable Long photoId){
        photoService.setHide(photoId);

        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("photo hide in article successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/tag")
    @Operation(summary = "keyword로 photo정보 가져오기")
    public ResponseEntity<ResponseDto<Page<PhotoTagResponse>>> searchPhotoByTags(
            @RequestParam(value = "keyword", required = false) List<String> keywords, Pageable pageable
    ){
        Page<PhotoTagResponse> byTagContaining = photoService.findByTagContaining(keywords, pageable);

        ResponseDto<Page<PhotoTagResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get Photo informations by keyword successful.");
        response.setData(byTagContaining);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{photoId}")
    @Operation(summary = "photo detail 정보조회")
    public ResponseEntity<ResponseDto<PhotoDetailResponse>> getDetail(@PathVariable Long photoId) {
        PhotoDetailResponse detailInformation = photoService.getDetailInformation(photoId);
        ResponseDto<PhotoDetailResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Photo get tag detail info successful.");
        response.setData(detailInformation);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/tag/{photoId}")
    @Operation(summary = "photo tag detail 정보조회")
    public ResponseEntity<ResponseDto<TagDetailResponse>> getTagDetail(@PathVariable Long photoId) {
        TagDetailResponse detailInformation = photoService.tagDetailResponse(photoId);
        ResponseDto<TagDetailResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Photo get tag detail info successful.");
        response.setData(detailInformation);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{photoId}")
    @Operation(summary = "photo 삭제")
    // photo 삭제시, travel, location 에서도 삭제되나 확인해야함
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long photoId) {
        photoService.delete(photoId);
        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Photo deletion successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
