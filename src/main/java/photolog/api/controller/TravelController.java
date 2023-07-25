package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photolog.api.domain.Theme;
import photolog.api.dto.ResponseDto;
import photolog.api.dto.travel.*;

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
    @Operation(summary = "travel에 사진 추가가 완료 후, 메타 정보 분석/계산")
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
    @Operation(summary = "여행 title 설정")
    public ResponseEntity<ResponseDto<String>> changeTitle(@PathVariable Long travelId,
                                                           @RequestBody TitleRequest request){
        String updateTitle = travelService.updateTitle(travelId, request);

        ResponseDto<String> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("set travel title successful.");
        response.setData(updateTitle);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/theme/{travelId}")
    @Operation(summary = "여행 theme 설정")
    public ResponseEntity<ResponseDto<Theme>> changeTitle(@PathVariable Long travelId,
                                                           @RequestBody ThemeRequest request){
        Theme theme = travelService.updateTheme(travelId, request);

        ResponseDto<Theme> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("set travel theme successful.");
        response.setData(theme);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/textSummary/{travelId}")
    @Operation(summary = "travel text summary 조회")
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
    @Operation(summary = "travel map summary 조회")
    public ResponseEntity<ResponseDto<SummaryMapResponse>> mapSummary(@PathVariable Long travelId) {
        SummaryMapResponse summaryMapResponse = travelService.mapSummary(travelId);

        ResponseDto<SummaryMapResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get travel map summary successful.");
        response.setData(summaryMapResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("")
    @Operation(summary = "내 travel log  조회")
    public ResponseEntity<ResponseDto<List<MyLogResponse>>> getMyLog() {
        List<MyLogResponse> myLog = travelService.getMyLog();

        ResponseDto<List<MyLogResponse>> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("get my travel log successful.");
        response.setData(myLog);

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
