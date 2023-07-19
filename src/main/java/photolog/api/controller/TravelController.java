package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.ResponseDto;
import photolog.api.dto.Travel.CalculateResponse;

import photolog.api.dto.Travel.SummaryMapResponse;
import photolog.api.dto.Travel.SummaryTextResponse;
import photolog.api.dto.Travel.TitleRequest;
import photolog.api.service.TravelService;

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
    @Operation(summary = "travel에 사진 추가가 완료된 후, 해당 api를 통해 나머지 정보 계산 후 계산된 결과 불러오기")
    public ResponseEntity<ResponseDto<CalculateResponse>> calTravel(@PathVariable Long travelId){
        CalculateResponse calculateResponse = travelService.calTravel(travelId);

        ResponseDto<CalculateResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Travel calculate successful.");
        response.setData(calculateResponse);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


    @PatchMapping("/title/{travelId}")
    @Operation(summary = "여행 title 변경")
    public ResponseEntity<ResponseDto<String>> changeTitle(@PathVariable Long travelId,
                                                           @RequestBody TitleRequest request){
        String updateTitle = travelService.updateTitle(travelId, request);

        ResponseDto<String> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get travel summary successful.");
        response.setData(updateTitle);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/textSummary/{travelId}")
    @Operation(summary = "travel summary 조회")
    public ResponseEntity<ResponseDto<SummaryTextResponse>> textSummary(@PathVariable Long travelId) {
        SummaryTextResponse summaryTextResponse = travelService.textSummary(travelId);

        ResponseDto<SummaryTextResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get travel text summary successful.");
        response.setData(summaryTextResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/mapSummary/{travelId}")
    @Operation(summary = "travel summary 조회")
    public ResponseEntity<ResponseDto<SummaryMapResponse>> mapSummary(@PathVariable Long travelId) {
        SummaryMapResponse summaryMapResponse = travelService.mapSummary(travelId);

        ResponseDto<SummaryMapResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get travel map summary successful.");
        response.setData(summaryMapResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping ("/{travelId}")
    @Operation(summary = "travel 삭제")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long travelId) {
        travelService.delete(travelId);
        ResponseDto<Void> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("travel deletion successful.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

}
