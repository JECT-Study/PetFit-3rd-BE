package ject.petfit.global.dev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ject.petfit.global.common.ApiResponse;
import ject.petfit.global.dev.service.DevService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Tag(name = "개발용 API")
@RequestMapping("/dev")
@RequiredArgsConstructor
public class DevBatchController {

    private final DevService devService;

    @PostMapping("/flush")
    @Operation(summary = "하루 기록 수동 업데이트",
            description = "1. 해당 날짜의 루틴 완료 여부 업데이트 <br>" +
                    "2. 해당 날짜의 미체크 루틴을 DB에 추가 <br> " +
                    "기록된 루틴 CHECKED, 루틴 MEMO, 특이사항, 일정이 있는 경우에만 업데이트")
    public ResponseEntity<ApiResponse<String>> flushEntries(
            @RequestParam LocalDate entryDate,
            @RequestParam Long petId
    ) {
        devService.entryDateFlush(petId, entryDate);
        return  ResponseEntity.ok(ApiResponse.success("완료"));
    }
}
