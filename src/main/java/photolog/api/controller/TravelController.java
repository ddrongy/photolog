package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.ResponseDto;
import photolog.api.dto.Travel.CalculateResultResponse;

import photolog.api.dto.Travel.TitleRequest;
import photolog.api.service.TravelService;

import java.util.List;

@Tag(name = "travel", description = "여행기록 API")
@RestController
@RequestMapping("/api/travel")
@RequiredArgsConstructor
public class TravelController {
    private final TravelService travelService;

    @PostMapping("/create")
    @Operation(summary = "travel 객체 초기 생성")
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
    @Operation(summary = "travel에 사진 추가가 완료된 후, 해당 api를 통해 나머지 정보 계산")
    public ResponseEntity<ResponseDto<List<Long>>> calTravel(@PathVariable Long travelId){
        List<Long> locationIds = travelService.calTravel(travelId);

        ResponseDto<List<Long>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Travel calculate successful.");
        response.setData(locationIds);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/calResult/{travelId}")
    @Operation(summary = "계산된 결과 불러오기, 총날짜, 시작날짜, 끝날짜 등..")
    public ResponseEntity<ResponseDto<CalculateResultResponse>> calResultTravel(@PathVariable Long travelId){
        CalculateResultResponse calculateResultResponse = travelService.calculateResult(travelId);

        ResponseDto<CalculateResultResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get travel calResult successful.");
        response.setData(calculateResultResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/title/{travelId}")
    @Operation(summary = "여행 title 변경")
    public ResponseEntity<ResponseDto<String>> changeTitle(@PathVariable Long travelId,
                                                           @RequestBody TitleRequest request){
        String updateTitle = travelService.updateTitle(travelId, request);

        ResponseDto<String> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("change travel title successful.");
        response.setData(updateTitle);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
