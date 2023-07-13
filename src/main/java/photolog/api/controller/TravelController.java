package photolog.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/calculate/{travelId}")
    public ResponseEntity<ResponseDto<Long>> calTravel(@PathVariable Long travelId){
        travelService.calTravel(travelId);

        ResponseDto<Long> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Travel create successful.");
        response.setData(travelId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


}
