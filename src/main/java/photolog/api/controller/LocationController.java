package photolog.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import photolog.api.dto.Location.DescriptionRequest;
import photolog.api.dto.Location.LocationResponse;
import photolog.api.dto.Location.NameRequest;
import photolog.api.dto.ResponseDto;
import photolog.api.dto.Travel.CalculateResultResponse;
import photolog.api.dto.Travel.TitleRequest;
import photolog.api.service.LocationService;

@Tag(name = "Location", description = "장소 API")
@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/{locationId}")
    @Operation(summary = "location 정보 불러오기")
    public ResponseEntity<ResponseDto<LocationResponse>> getLocation(@PathVariable Long locationId){

        LocationResponse location = locationService.getLocation(locationId);

        ResponseDto<LocationResponse> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Get location information successful.");
        response.setData(location);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/description/{locationId}")
    @Operation(summary = "장소 description 변경")
    public ResponseEntity<ResponseDto<String>> changeDescription(@PathVariable Long locationId,
                                                                 @RequestBody DescriptionRequest request){
        String updateDescription = locationService.updateDescription(locationId, request);

        ResponseDto<String> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Change location description successful.");
        response.setData(updateDescription);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PatchMapping("/name/{locationId}")
    @Operation(summary = "장소 name 변경")
    public ResponseEntity<ResponseDto<String>> changeName(@PathVariable Long locationId,
                                                          @RequestBody NameRequest request){
        String updateDescription = locationService.updateName(locationId, request);

        ResponseDto<String> response = new ResponseDto<>();
        response.setStatus(true);
        response.setMessage("Change location name successful.");
        response.setData(updateDescription);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
