package ject.petfit.domain.aireport.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.domain.aireport.dto.request.DateRequestDto;
import ject.petfit.domain.aireport.dto.response.AiReportResponseDto;
import ject.petfit.domain.aireport.facade.AiReportFacade;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aireports")
@Tag(name = "AiReport", description = "AI 인사이트 보고서 API")
public class AiReportController {

    private final AiReportFacade aiReportFacade;

    @GetMapping("/{reportId}")
    @Operation(summary = "AI 인사이트 보고서 조회", description = "reportId로 AI 인사이트 보고서 조회")
    public ResponseEntity<ApiResponse<AiReportResponseDto>> getAiReport(
            @PathVariable Long reportId
    ) {
        AiReportResponseDto responseDto = aiReportFacade.getAiReport(reportId);
        return ResponseEntity.ok(
                ApiResponse.success(responseDto)
        );
    }

    @PostMapping("/generate")
    @Operation(summary = "AI 인사이트 보고서 생성", description = "펫 ID와 날짜 범위를 받아 AI 인사이트 보고서 생성 요청")
    public ResponseEntity<ApiResponse<AiReportResponseDto>> generateAiReport(
            @RequestBody DateRequestDto dateRequestDto
            ) throws JsonProcessingException {
        AiReportResponseDto responseDto = aiReportFacade.createAiReport(dateRequestDto);
        return ResponseEntity.ok(
                ApiResponse.success(responseDto)
        );
    }

    @GetMapping("/list")
    @Operation(summary = "AI 인사이트 보고서 목록 조회", description = "펫 ID로 AI 인사이트 보고서 목록을 조회합니다")
    public ResponseEntity<ApiResponse<List<AiReportResponseDto>>> getAiReportsByPetId(
            @RequestParam Long petId
    ) {
        List<AiReportResponseDto> responseDtos = aiReportFacade.getAiReportsByPetId(petId);
        return ResponseEntity.ok(
                ApiResponse.success(responseDtos)
        );
    }

    @DeleteMapping("/{reportId}")
    @Operation(summary = "AI 인사이트 보고서 삭제", description = "reportId로 AI 인사이트 보고서를 삭제합니다")
    public ResponseEntity<ApiResponse<String>> deleteAiReport(
            @PathVariable Long reportId
    ) {
        aiReportFacade.deleteAiReport(reportId);
        return ResponseEntity.ok(
                ApiResponse.success("AI 인사이트 보고서가 성공적으로 삭제되었습니다.")
        );
    }
}
