package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.domain.TourData;
import photolog.api.dto.ResponseDto;
import photolog.api.service.TourDataService;

import java.util.List;

@Tag(name = "tour", description = "tourData API")
@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class TourDataController {
    private final TourDataService tourDataService;

    @GetMapping("/contentId/{contentId}")
    @Operation(summary = "contentID로 tourDATA 검색하기")
    public ResponseEntity<ResponseDto<TourData>> searchByContentId(@PathVariable Long contentId){

        TourData tourData = tourDataService.searchByContentId(contentId);

        ResponseDto<TourData> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get tourData information successful.");
        response.setData(tourData);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/tag")
    @Operation(summary = "keyword로 tourData정보 가져오기")
    public ResponseEntity<ResponseDto<List<TourData>>> getOtherLocations(
            @RequestParam(required = false) String keyword
    ){

        List<TourData> otherLocations = tourDataService.findByTagContaining(keyword);

        ResponseDto<List<TourData>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get Tourdata informations by keyword successful.");
        response.setData(otherLocations);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
