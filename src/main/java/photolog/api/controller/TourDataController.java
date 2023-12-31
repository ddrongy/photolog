package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import photolog.api.dto.ResponseDto;

import photolog.api.dto.tour.TagDetailResponse;
import photolog.api.dto.tour.TourResponse;
import photolog.api.service.TourService;

import java.io.IOException;
import java.util.List;

@Tag(name = "tour", description = "tourData API")
@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourDataController {
    private final TourService tourService;

    @GetMapping("/contentId/{contentId}")
    @Operation(summary = "contentID로 tourDATA 검색하기")
    public ResponseEntity<ResponseDto<TagDetailResponse>> searchByContentId(@PathVariable Long contentId) throws IOException {

        TagDetailResponse tourData = tourService.searchByContentId(contentId);

        ResponseDto<TagDetailResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get tourData information successful.");
        response.setData(tourData);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/tag")
    public ResponseEntity<ResponseDto<Page<TourResponse>>> getOtherLocations(
            @RequestParam(value = "keyword", required = false) List<String> keywords, Pageable pageable
    ){
        Page<TourResponse> otherLocations = tourService.findByTagContaining(keywords, pageable);

        ResponseDto<Page<TourResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get Tourdata informations by keyword successful.");
        response.setData(otherLocations);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @PostMapping("/bookmark/{tourId}")
    @Operation(summary = "게시글 bookmark")
    public ResponseEntity<ResponseDto<Integer>> addBookmark(@PathVariable Long tourId) {
        Integer bookmarkCount = tourService.addBookmark(tourId);

        ResponseDto<Integer> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("add bookmark successful.");
        response.setData(bookmarkCount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/bookmark/{tourId}")
    @Operation(summary = "게시글 bookmark 취소")
    public ResponseEntity<ResponseDto<Integer>> cancelBookmark(@PathVariable Long tourId) {
        Integer bookmarkCount = tourService.cancelBookmark(tourId);

        ResponseDto<Integer> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("cancel bookmark successful.");
        response.setData(bookmarkCount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


}
