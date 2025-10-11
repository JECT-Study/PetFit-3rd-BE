package ject.petfit.domain.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import ject.petfit.domain.alarm.dto.request.AlarmRegisterRequest;
import ject.petfit.domain.alarm.dto.request.AlarmUpdateRequest;
import ject.petfit.domain.alarm.dto.response.AlarmResponse;
import ject.petfit.domain.alarm.facade.AlarmFacade;
import ject.petfit.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
@Tag(name = "Alarm", description = "알람 API")
public class AlarmController {
    private final AlarmFacade alarmFacade;

    /*
     * 클라이언트가 /api/alarms/subscribe에 접속하면 서버와의 연결이 유지되고,
     * 서버에서 SseEmitter를 통해 알림이 발생할 때마다 즉시 데이터를 푸시할 수 있습니다.
     *
     * 클라이언트가 구독을 시작하면 서버가 실시간으로 알림을 전송할 수 있는 상태가 됩니다.
     * 실제로 "실시간"으로 알림이 전송되는지는 sseEmitterService.createEmitter()와 알림 발생 시점에 따라 다릅니다.
     */
    @GetMapping(value = "/{petId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE 연결 요청")
    public SseEmitter subscribe(
            HttpServletRequest request,
            @PathVariable Long petId
    ) {
//        log.info("SSE 구독 요청 받음: {}", request.getRequestURL());
        return alarmFacade.createEmitter(petId);
    }

    @PostMapping("/{petId}")
    @Operation(summary = "알람 등록")
    public ResponseEntity<ApiResponse<Void>> registerAlarm(
            @RequestBody AlarmRegisterRequest request,
            @PathVariable Long petId
    ) {
//        log.info("등록 petId: {}", petId);
        alarmFacade.saveAlarm(request, petId);
        return ResponseEntity.status(201).body(ApiResponse.success(null));
    }

    @GetMapping("/{petId}")
    @Operation(summary = "모든 알람 리스트 조회")
    public ResponseEntity<ApiResponse<List<AlarmResponse>>> getAllAlarms(
            @PathVariable Long petId
    ) {
        return ResponseEntity.ok(ApiResponse.success(alarmFacade.getAllAlarms(petId)));
    }

    @GetMapping("/{petId}/unread")
    @Operation(summary = "읽지 않은 알람 리스트 조회", description = "읽지 않은 모든 알람을 최신순으로 조회")
    public ResponseEntity<ApiResponse<List<AlarmResponse>>> getAllUnreadAlarms(
            @PathVariable Long petId
    ) {
//        log.info("조회 petId: {}", petId);
        return ResponseEntity.ok(ApiResponse.success(alarmFacade.getAllUnreadAlarms(petId)));
    }

    @GetMapping("/{petId}/home")
    @Operation(summary = "홈화면 알람 리스트 조회", description = "3일 이내의 모든 알람을 조회")
    public ResponseEntity<ApiResponse<List<AlarmResponse>>> getHomeAlarms(
            @PathVariable Long petId
    ){
        return ResponseEntity.ok(ApiResponse.success(alarmFacade.getHomeAlarms(petId)));
    }

    @DeleteMapping("/{alarmId}")
    @Operation(summary = "알람 삭제")
    public ResponseEntity<ApiResponse<String>> deleteAlarm(@PathVariable Long alarmId) {
        alarmFacade.deleteAlarm(alarmId);
        return ResponseEntity.ok(ApiResponse.success("알람 삭제 성공"));
    }

    @PatchMapping("/{alarmId}/read")
    @Operation(summary = "알람 읽음 처리")
    public void markAsRead(@PathVariable Long alarmId) {
//        log.info("{}번 알람 읽음 처리", alarmId);
        alarmFacade.markAsRead(alarmId);
    }

    @PatchMapping("/{petId}/all/read")
    @Operation(summary = "알람 일괄 읽음 처리")
    public void markAllAsRead(@PathVariable Long petId) {
//        log.info("{}번 펫 알람 일괄 읽음 처리", petId);
        alarmFacade.markAllAsRead(petId);
    }

    @PatchMapping("/{alarmId}")
    @Operation(summary = "알람 수정")
    public ResponseEntity<ApiResponse<AlarmResponse>> updateAlarm(
            @PathVariable Long alarmId,
            @RequestBody AlarmUpdateRequest alarmUpdateRequest
    ) {
//        log.info("{}번 알람 수정", alarmId);
        return ResponseEntity.ok(ApiResponse.success(alarmFacade.updateAlarm(alarmId, alarmUpdateRequest)));
    }

}
